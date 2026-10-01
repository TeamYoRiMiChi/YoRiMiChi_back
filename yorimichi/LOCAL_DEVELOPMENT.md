# 로컬 MySQL 인증과 Cognito/RDS 인증

## 로컬 MySQL만 사용

`src/main/resources/application.yml`의 기본 프로필은 `local`입니다.
`local`에서는 Spring이 `MEMBER.password`의 BCrypt 해시를 검증하고 자체 JWT를 발급합니다.
Cognito, RDS, SSM 터널, AWS 자격증명 없이 가입·로그인·회원 조회가 가능합니다.
기존 로컬 테이블에는 `cognito_sub`를 추가할 필요가 없습니다.

1. 로컬 MySQL을 실행하고 기존 프로젝트 DB 스키마를 준비합니다.
   회원 테이블만 새로 만들려면 `scripts/local-member.sql`을 로컬 DB에서 실행합니다.
   이 파일은 상품·주문·배송지 등 다른 테이블을 생성하지 않습니다.
2. 아래 `local` 프로필의 접속 정보를 본인 MySQL에 맞춥니다.
   기본값: `localhost:3306/yorimichi_db`, 계정/비밀번호 `yorimichi`.
   `LOCAL_DB_URL`, `LOCAL_DB_USERNAME`, `LOCAL_DB_PASSWORD` 환경변수로도 지정할 수 있습니다.
3. backend `yorimichi` 폴더에서 실행:

   ```powershell
   .\gradlew.bat clean bootRun --args="--spring.profiles.active=local"
   ```

   STS/Eclipse는 프로필을 `local`로 선택하고 Project → Clean 후 실행합니다.
   회원 매퍼 경로가 변경됐으므로 이전 `bin/main/mapper/user/UserMapper.xml`이
   빌드 출력에 남지 않도록 최초 한 번 Clean이 필요합니다.
4. 프론트 폴더에서 `npm run dev` 실행 후 `http://localhost:5173/login`으로 접속합니다.
   개발 환경의 `VITE_API_BASE_URL`은 비우거나 `http://localhost:9000/api`로 설정합니다.
   프론트가 `/api/auth/config`를 조회하므로 별도의 인증 모드 환경변수는 필요 없습니다.

기존 BCrypt 비밀번호는 유지됩니다. Cognito 계정은 로컬 MySQL에 자동 복제되지 않으므로
로컬 회원이 없으면 로컬 화면에서 가입합니다. 평문 비밀번호는 지원하지 않습니다.
로컬 가입은 이메일 확인 없이 완료됩니다. Google 로그인과 이메일 비밀번호 재설정은
클라우드 전용이며 로컬에서 노출하지 않습니다. 서버 재시작 시 로컬 JWT가 만료되어 재로그인해야 합니다.

## Cognito + RDS 사용

프로필을 `rds`로 선택하면 기존 Cognito 인증과 RDS 회원 조회가 동작합니다.
RDS 연결용 SSM 터널과 `DB_USERNAME`, `DB_PASSWORD`를 먼저 준비합니다.

```powershell
.\gradlew.bat clean bootRun --args="--spring.profiles.active=rds"
```

RDS에는 `cognito_sub` 기반의 기존 스키마가 필요합니다. 로컬용 SQL을 RDS에 실행하지 않습니다.
Docker 이미지의 기본 프로필은 명시적으로 `rds`이며 ECS 인증 방식은 유지됩니다.
동시에 `local,rds` 두 프로필을 활성화하지 않습니다.

프론트 배포 시 `/api/auth/config`를 제공하는 새 백엔드를 먼저 배포합니다.
프로필을 전환한 뒤 프론트 페이지를 새로고침하면 인증 방식이 다시 감지됩니다.

## 검증

```powershell
.\gradlew.bat test --tests "*LocalAuthenticationIntegrationTest" --tests "*LocalAdminPolicyTest"
```

통합 테스트는 Cognito 컬럼이 없는 H2 MySQL 호환 테스트 DB에서 실제 HTTP 요청으로
가입, 중복 가입, 잘못된 비밀번호, JWT 인증, 회원 조회, 다른 보호 API, 권한과 계정 상태를 검사합니다.
실제 로컬 MySQL 계정과 테이블 준비는 별도로 필요합니다.
