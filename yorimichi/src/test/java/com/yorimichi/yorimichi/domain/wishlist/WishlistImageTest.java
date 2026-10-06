package com.yorimichi.yorimichi.domain.wishlist;

import com.yorimichi.yorimichi.domain.mypage.dto.WishlistResponseDto;
import com.yorimichi.yorimichi.domain.mypage.repository.WishlistMapper;
import com.yorimichi.yorimichi.domain.wishlist.dto.WishlistItemResponseDto;
import com.yorimichi.yorimichi.domain.wishlist.repository.ProductWishlistMapper;
import com.yorimichi.yorimichi.global.storage.ImageStorageService;
import com.yorimichi.yorimichi.global.storage.ImageUrlResolver;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

import java.sql.Timestamp;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WishlistImageTest {
    // H2 compatibility for the unrelated group-buy deadline expression.
    public static Timestamp dateAdd(Timestamp date, String interval) {
        return date == null ? null : Timestamp.valueOf(date.toLocalDateTime().plusDays(3));
    }

    @Test
    @SuppressWarnings("unchecked")
    void bothWishlistQueriesReturnUploadedImagesAndResolveStorageKeys() throws Exception {
        var dataSource = new UnpooledDataSource("org.h2.Driver",
                "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        try (var connection = dataSource.getConnection(); var sql = connection.createStatement()) {
            sql.execute("CREATE ALIAS DATE_ADD FOR 'com.yorimichi.yorimichi.domain.wishlist.WishlistImageTest.dateAdd'");
            sql.execute("CREATE TABLE PRODUCT (product_id BIGINT PRIMARY KEY, sale_type VARCHAR(20), "
                    + "brand VARCHAR(50), product_name VARCHAR(50), product_name_jp VARCHAR(50), "
                    + "price_jpy DECIMAL, original_price_jpy DECIMAL, thumbnail_url VARCHAR(500), "
                    + "stock INT, status VARCHAR(20))");
            sql.execute("CREATE TABLE PRODUCT_IMAGE (image_id BIGINT PRIMARY KEY, product_id BIGINT, "
                    + "image_url VARCHAR(500), image_order INT, is_thumbnail INT)");
            sql.execute("CREATE TABLE WISHLIST (wishlist_id BIGINT PRIMARY KEY, member_id BIGINT, "
                    + "product_id BIGINT, created_at TIMESTAMP, updated_at TIMESTAMP)");
            sql.execute("CREATE TABLE GROUP_BUY (group_buy_id BIGINT PRIMARY KEY, product_id BIGINT, "
                    + "status VARCHAR(20), current_quantity INT, target_quantity INT, end_date TIMESTAMP)");
            sql.execute("INSERT INTO PRODUCT VALUES "
                    + "(1,'OVERSEAS','brand','uploaded',null,20000,null,null,94,'ACTIVE'),"
                    + "(2,'OVERSEAS','brand','legacy',null,1000,null,'https://legacy.example/image.jpg',1,'ACTIVE'),"
                    + "(3,'OVERSEAS','brand','empty',null,1000,null,null,1,'ACTIVE')");
            sql.execute("INSERT INTO WISHLIST VALUES "
                    + "(1,7,1,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),"
                    + "(2,7,2,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),"
                    + "(3,7,3,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),"
                    + "(4,8,1,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)");
            sql.execute("INSERT INTO PRODUCT_IMAGE VALUES "
                    + "(1,1,'products/first.png',0,0), (2,1,'products/selected.gif',1,1)");
        }

        var config = new Configuration(new Environment("test", new JdbcTransactionFactory(), dataSource));
        for (String resource : new String[]{"mapper/wishlist/ProductWishlistMapper.xml", "mapper/mypage/WishlistMapper.xml"}) {
            try (var input = Resources.getResourceAsStream(resource)) {
                // H2 requires a quoted interval literal; image selection SQL remains unchanged.
                String xml = new String(input.readAllBytes(), StandardCharsets.UTF_8)
                        .replace("INTERVAL 3 DAY", "INTERVAL '3' DAY");
                new XMLMapperBuilder(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)),
                        config, resource, config.getSqlFragments()).parse();
            }
        }
        var storage = mock(ImageStorageService.class);
        when(storage.getUrl(anyString())).thenAnswer(call -> "https://images.example/" + call.getArgument(0));
        ObjectProvider<ImageStorageService> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(storage);
        new ImageUrlResolver(provider);
        try (var session = new SqlSessionFactoryBuilder().build(config).openSession()) {
            var drawer = session.getMapper(ProductWishlistMapper.class).findByMemberId(7L).stream()
                    .map(WishlistItemResponseDto::new).toList();
            var mypage = session.getMapper(WishlistMapper.class).findAllByMemberId(7L).stream()
                    .map(WishlistResponseDto::new).toList();
            assertThat(drawer).hasSize(3);
            assertThat(mypage).hasSize(3);
            assertThat(drawer).extracting(WishlistItemResponseDto::getThumbnailUrl)
                    .containsExactlyInAnyOrder("https://images.example/products/selected.gif", "https://legacy.example/image.jpg", null);
            assertThat(mypage).extracting(WishlistResponseDto::getThumbnailUrl)
                    .containsExactlyInAnyOrder("https://images.example/products/selected.gif", "https://legacy.example/image.jpg", null);
            session.getConnection().createStatement().execute("DELETE FROM PRODUCT_IMAGE WHERE image_id = 2");
            session.clearCache();
            assertThat(session.getMapper(ProductWishlistMapper.class).findByMemberId(7L))
                    .filteredOn(item -> item.getProductId().equals(1L))
                    .extracting(item -> new WishlistItemResponseDto(item).getThumbnailUrl())
                    .containsExactly("https://images.example/products/first.png");
        } finally {
            new ImageUrlResolver(mock(ObjectProvider.class));
        }
    }
}
