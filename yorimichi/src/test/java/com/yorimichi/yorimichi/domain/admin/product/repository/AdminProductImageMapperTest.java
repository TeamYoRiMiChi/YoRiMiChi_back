package com.yorimichi.yorimichi.domain.admin.product.repository;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AdminProductImageMapperTest {

    @Test
    void registersKeysAndRollsBackWithRealMapperSql() throws Exception {
        var dataSource = new UnpooledDataSource("org.h2.Driver",
                "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        try (var connection = dataSource.getConnection(); var statement = connection.createStatement()) {
            statement.execute("CREATE TABLE PRODUCT (product_id BIGINT PRIMARY KEY, is_deleted INT)");
            statement.execute("CREATE TABLE PRODUCT_IMAGE (image_id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                    + "product_id BIGINT, image_url VARCHAR(500), image_order INT, is_thumbnail INT)");
            statement.execute("INSERT INTO PRODUCT VALUES (7, 0), (8, 1)");
        }

        var configuration = new Configuration(new Environment("test", new JdbcTransactionFactory(), dataSource));
        configuration.setMapUnderscoreToCamelCase(true);
        String resource = "mapper/admin/AdminProductImageMapper.xml";
        try (var input = Resources.getResourceAsStream(resource)) {
            new XMLMapperBuilder(input, configuration, resource, configuration.getSqlFragments()).parse();
        }
        var factory = new SqlSessionFactoryBuilder().build(configuration);
        try (var session = factory.openSession(false)) {
            var mapper = session.getMapper(AdminProductImageMapper.class);
            assertThat(mapper.lockProduct(7L)).isEqualTo(7L);
            assertThat(mapper.lockProduct(8L)).isNull();
            assertThat(mapper.lockProduct(9L)).isNull();
            String key = "products/15/12345678-1234-1234-1234-123456789abc.gif";
            mapper.insert(7L, key, 0, true);
            assertThat(mapper.countByImageKey(key)).isEqualTo(1);
            assertThat(mapper.countByProductId(7L)).isEqualTo(1);
            assertThat(mapper.countThumbnail(7L)).isEqualTo(1);
            assertThat(mapper.findByProductId(7L)).hasSize(1);
            session.rollback();
            assertThat(mapper.countByProductId(7L)).isZero();
        }
    }
}
