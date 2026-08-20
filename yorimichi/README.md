## 프로젝트 구조

### Frontend (React + Vite)

React 컴포넌트, 상태 관리(Redux), API 통신(axios), 라우팅을 기능별로 나눈 구조입니다.

\`\`\`
src
├── main.jsx                  # 앱 진입점. Redux Provider, Router를 여기서 연결
├── App.jsx                   # 최상위 컴포넌트
├── index.css                 # 전역 스타일 진입점 (assets/styles 파일들을 불러옴)
│
├── api                       # axios로 백엔드와 통신하는 함수 모음
│   ├── axiosInstance.js      # axios 공통 설정. baseURL, 요청 시 토큰 자동 첨부, 공통 에러 처리
│   ├── userApi.js            # User 도메인 API 함수 (회원가입, 로그인, 조회 등)
│   ├── authApi.js            # 인증/OAuth 관련 API 함수
│   └── boardApi.js           # 게시판 도메인 API 함수
│
├── app
│   └── store.js              # Redux 스토어 설정. features의 slice들을 등록
│
├── features                  # Redux Toolkit slice. 도메인별로 전역 상태 관리
│   ├── auth
│   │   └── authSlice.js      # 로그인 토큰, 사용자 정보 상태 관리
│   ├── user
│   │   └── userSlice.js
│   └── board
│       └── boardSlice.js
│
├── pages                     # 라우트에 직접 매칭되는 페이지 컴포넌트
│   ├── Home.jsx
│   ├── Login.jsx
│   ├── Signup.jsx
│   ├── MyPage.jsx
│   └── board
│       ├── BoardList.jsx
│       └── BoardDetail.jsx
│
├── components                # 여러 페이지에서 재사용되는 UI 컴포넌트
│   ├── common                # 버튼, 인풋, 모달 등 범용 컴포넌트
│   │   ├── Button.jsx
│   │   ├── Input.jsx
│   │   └── Modal.jsx
│   └── layout                # 레이아웃 관련 컴포넌트
│       ├── Header.jsx / Header.css
│       ├── Footer.jsx / Footer.css
│       └── Layout.jsx / Layout.css   # Header + Footer + 페이지 콘텐츠를 감싸는 공통 레이아웃
│
├── routes
│   ├── Router.jsx             # 전체 라우팅 설정 (경로 ↔ 페이지 매칭)
│   └── PrivateRoute.jsx       # 로그인 필요한 페이지 접근 제어 (비로그인 시 /login으로 리다이렉트)
│
├── hooks
│   └── useAuth.js             # 로그인 상태 등 반복되는 로직을 재사용하기 위한 커스텀 훅
│
├── utils                      # 도메인과 무관한 순수 유틸 함수
│   ├── validation.js          # 입력값 검증 함수
│   └── formatDate.js          # 날짜 포맷 변환 함수
│
└── assets
    ├── images
    └── styles                 # 전역 CSS
        ├── variables.css      # 색상, 간격, 폰트 크기 등 CSS 변수 정의
        ├── reset.css          # 브라우저 기본 스타일(margin, padding 등) 초기화
        ├── typography.css     # 폰트, 텍스트 관련 공통 스타일
        ├── common.css         # 버튼, 인풋, 카드 등 여러 컴포넌트에서 재사용하는 공통 클래스
        └── utils.css          # flex, margin 등 자주 쓰는 유틸리티 클래스
\`\`\`

**폴더별 역할 요약**
| 폴더 | 역할 |
|---|---|
| `api` | 백엔드 API 호출 함수 (컴포넌트는 이 함수만 import해서 사용) |
| `features` | Redux 전역 상태 (로그인 토큰, 사용자 정보 등) |
| `pages` | 라우트와 1:1로 매칭되는 화면 |
| `components` | 여러 페이지에서 재사용되는 조각 UI |
| `routes` | URL 경로와 페이지를 연결, 로그인 여부에 따른 접근 제어 |
| `hooks` | 여러 컴포넌트에서 반복되는 로직을 재사용하기 위한 커스텀 훅 |

---

### Backend (Spring Boot)

패키지를 계층(controller/service/repository)이 아닌 **기능(도메인) 단위**로 나눈 구조입니다. 도메인이 늘어나도 기존 코드에 영향을 주지 않고 확장할 수 있습니다.

\`\`\`
com.nexlify.nexlify
│
├── NexlifyApplication.java    # 스프링 부트 실행 진입점
│
├── global                     # 여러 도메인에서 공통으로 사용하는 설정/기능
│   ├── config
│   │   ├── SecurityConfig.java    # Spring Security, JWT 인증, CORS 설정
│   │   ├── WebConfig.java         # CORS 등 MVC 관련 설정
│   │   ├── RedisConfig.java       # Redis 연결 설정
│   │   ├── MyBatisConfig.java     # MyBatis Mapper 스캔 설정
│   │   └── JpaConfig.java         # JPA Auditing 등 설정
│   │
│   ├── error                      # 예외 처리
│   │   ├── GlobalExceptionHandler.java  # 모든 예외를 한 곳에서 잡아 공통 응답으로 변환
│   │   ├── CustomException.java         # 비즈니스 로직에서 던지는 커스텀 예외
│   │   ├── ErrorCode.java               # 에러 코드/메시지 정의 (enum)
│   │   └── ErrorResponse.java           # 에러 발생 시 클라이언트에 내려줄 응답 형태
│   │
│   ├── jwt                        # JWT 인증
│   │   ├── JwtProvider.java             # 토큰 생성/검증
│   │   ├── JwtAuthenticationFilter.java # 요청마다 토큰 검사하는 필터
│   │   └── JwtProperties.java           # yml의 jwt 설정값 바인딩
│   │
│   ├── util
│   │   ├── FileUploadUtil.java    # 파일 업로드/삭제 공통 로직
│   │   └── ModelMapperConfig.java # ModelMapper Bean 설정
│   │
│   └── response
│       └── ApiResponse.java       # 모든 API가 공통으로 쓰는 응답 포맷 { success, data, message }
│
├── domain                     # 도메인(기능)별 패키지. 각 도메인은 아래 구조를 따름:
│   │                          #   controller / service / repository / entity / dto
│   │
│   ├── user                   # 회원 관리 (회원가입, 조회 등)
│   ├── auth                   # 로그인, 소셜 로그인, JWT 발급
│   │   └── service/oauth
│   │       ├── KakaoOAuthService.java
│   │       ├── NaverOAuthService.java
│   │       └── GoogleOAuthService.java
│   ├── mail                   # 이메일 발송
│   ├── sms                    # SMS 인증 (coolsms 연동)
│   └── file                   # 파일 업로드/다운로드
│
└── infra                      # 외부 시스템 연동
    └── python
        └── PythonApiClient.java   # Python 서버와 통신하는 클라이언트
\`\`\`

**패키지별 역할 요약**
| 패키지 | 역할 |
|---|---|
| `global` | 도메인에 속하지 않는 공통 기능 (설정, 예외 처리, JWT, 응답 포맷) |
| `domain` | 실제 비즈니스 기능. 도메인 하나가 controller~dto까지 다 포함 |
| `infra` | 외부 시스템(Python 서버 등)과의 연동 코드 |

**리소스 폴더 (MyBatis 사용 도메인)**
\`\`\`
src/main/resources
├── application.yml
└── mapper                     # domain 패키지 구조와 이름을 맞춤
    ├── user
    │   └── UserMapper.xml      # domain/user/repository/UserMapper.java 와 1:1 매칭
    └── (도메인 늘어날 때마다 하위 폴더 추가)
\`\`\`