package com.maskapai;

import io.netty.handler.codec.http.HttpHeaderNames;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ratpack.http.client.ReceivedResponse;
import ratpack.test.CloseableApplicationUnderTest;
import ratpack.test.MainClassApplicationUnderTest;
import ratpack.test.http.TestHttpClient;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ApplicationTest {

    private final CloseableApplicationUnderTest appUnderTest = new MainClassApplicationUnderTest(AppMain.class);
    private final TestHttpClient httpClient = appUnderTest.getHttpClient();
    private final String TOKEN = "eyJhbGciOiJBMTI4S1ciLCJlbmMiOiJBMTI4Q0JDLUhTMjU2In0.VoAEWcNDXnIjkjXsHKg-BW29lFCh_oJHpKjnxU9EOiu7M_YbyS8-sg.ABBidYwO_z5q39P1RFrBOw.F0phhvnnJeLkQgTn6bR7acXBDRTzcVf0NZX0P4Nkw0gsz-N1mXU4ukx42zw32VGK.WAduf3cCrp3urWUYewjQqQ";

    @Test
    public void post_Login_succes() {
        ReceivedResponse response = httpClient
                .requestSpec(r -> r
                        .body(b -> b.text("username=foo&password=bar"))
                        .headers(h -> h
                                .set(HttpHeaderNames.CONTENT_TYPE, "application/x-www-form-urlencoded")
                        )
                )
                .params(m -> m.put("client_name", "FormClient"))
                .post("/login");

        assertEquals("{\"token\":\"eyJhbGciOiJBMTI4S1ciLCJlbmMiOiJBMTI4Q0JDLUhTMjU2In0", response.getBody().getText().split("\\.")[0]);
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void post_Login_fail() {
        ReceivedResponse response = httpClient
                .requestSpec(r -> r
                        .body(b -> b.text("username=foo&password=bar1"))
                        .headers(h -> h
                                .set(HttpHeaderNames.CONTENT_TYPE, "application/x-www-form-urlencoded")
                        )
                )
                .params(m -> m.put("client_name", "FormClient"))
                .post("/login");

        assertEquals("Client error 401", response.getBody().getText());
        assertEquals(401, response.getStatusCode());
    }

    @Test
    public void get_refrigerator1_exist() {
        ReceivedResponse response = httpClient
                .requestSpec(r -> r
                        .headers(h -> h
                                .set(HttpHeaderNames.CONTENT_TYPE, "application/json")
                                .set("token", TOKEN)
                        )
                )
                .get("/refrigerator/1");

        assertEquals("{\"id\":\"1\",\"items\":{\"pizza\":2,\"soda\":3}}", response.getBody().getText());
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void get_refrigerator1_exist_wrongToken() {
        ReceivedResponse response = httpClient
                .requestSpec(r -> r
                        .headers(h -> h
                                .set(HttpHeaderNames.CONTENT_TYPE, "application/json")
                                .set("token", "wrong")
                        )
                )
                .get("/refrigerator/1");

        assertEquals("Client error 401", response.getBody().getText());
        assertEquals(401, response.getStatusCode());
    }

    @Test
    public void get_refrigerator1_not_exist() {
        ReceivedResponse response = httpClient
                .requestSpec(r -> r
                        .headers(h -> h
                                .set(HttpHeaderNames.CONTENT_TYPE, "application/json")
                                .set("token", TOKEN)
                        )
                )
                .get("/refrigerator/4");

        assertEquals("Client error 404", response.getBody().getText());
        assertEquals(404, response.getStatusCode());
    }

    @Test
    public void delete_refrigerator3_exist() {
        ReceivedResponse response = httpClient
                .requestSpec(r -> r
                        .headers(h -> h
                                .set(HttpHeaderNames.CONTENT_TYPE, "application/json")
                                .set("token", TOKEN)
                        )
                )
                .delete("/refrigerator/3");

        assertEquals("null", response.getBody().getText());
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void delete_refrigerator4_not_exist() {
        ReceivedResponse response = httpClient
                .requestSpec(r -> r
                        .headers(h -> h
                                .set(HttpHeaderNames.CONTENT_TYPE, "application/json")
                                .set("token", TOKEN)
                        )
                )
                .delete("/refrigerator/4");

        assertEquals("Client error 404", response.getBody().getText());
        assertEquals(404, response.getStatusCode());
    }

    @Test
    public void get_refrigerator1_item_exist() {
        ReceivedResponse response = httpClient
                .requestSpec(r -> r
                        .headers(h -> h
                                .set(HttpHeaderNames.CONTENT_TYPE, "application/json")
                                .set("token", TOKEN)
                        )
                )
                .get("/refrigerator/1/item/soda");

        assertEquals("{\"item\":{\"soda\":3},\"refrigerator\":\"1\"}", response.getBody().getText());
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void get_refrigerator1_item_not_exist() {
        ReceivedResponse response = httpClient
                .requestSpec(r -> r
                        .headers(h -> h
                                .set(HttpHeaderNames.CONTENT_TYPE, "application/json")
                                .set("token", TOKEN)
                        )
                )
                .get("/refrigerator/1/item/meat");

        assertEquals("Client error 404", response.getBody().getText());
        assertEquals(404, response.getStatusCode());
    }

    @Test
    public void post_refrigerator1_item_under12() {
        ReceivedResponse response = httpClient
                .requestSpec(r -> r
                        .body(b -> b.text("{\n" +
                                "\t\"soda\": \"2\",\n" +
                                "\t\"pizza\": \"6\",\n" +
                                "\t\"meat\": \"5\"\n" +
                                "}"))
                        .headers(h -> h
                                .set(HttpHeaderNames.CONTENT_TYPE, "application/json")
                                .set("token", TOKEN)
                        )
                )
                .post("/refrigerator/1/item");

        assertEquals("{\"id\":\"1\",\"items\":{\"pizza\":8,\"meat\":5,\"soda\":5}}", response.getBody().getText());
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void post_refrigerator1_item_over12() {
        ReceivedResponse response = httpClient
                .requestSpec(r -> r
                        .body(b -> b.text("{\n" +
                                "\t\"soda\": \"20\",\n" +
                                "\t\"pizza\": \"6\",\n" +
                                "\t\"meat\": \"5\"\n" +
                                "}"))
                        .headers(h -> h
                                .set(HttpHeaderNames.CONTENT_TYPE, "application/json")
                                .set("token", TOKEN)
                        )
                )
                .post("/refrigerator/1/item");

        assertEquals("{\"id\":\"1\",\"items\":{\"pizza\":8,\"meat\":5,\"soda\":12}}", response.getBody().getText());
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void put_refrigerator1_item_under12() {
        ReceivedResponse response = httpClient
                .requestSpec(r -> r
                        .body(b -> b.text("{\n" +
                                "\t\"soda\": \"2\",\n" +
                                "\t\"pizza\": \"6\",\n" +
                                "\t\"meat\": \"5\"\n" +
                                "}"))
                        .headers(h -> h
                                .set(HttpHeaderNames.CONTENT_TYPE, "application/json")
                                .set("token", TOKEN)
                        )
                )
                .put("/refrigerator/1/item");

        assertEquals("{\"id\":\"1\",\"items\":{\"pizza\":6,\"meat\":5,\"soda\":2}}", response.getBody().getText());
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void put_refrigerator1_item_over12() {
        ReceivedResponse response = httpClient
                .requestSpec(r -> r
                        .body(b -> b.text("{\n" +
                                "\t\"soda\": \"20\",\n" +
                                "\t\"pizza\": \"60\",\n" +
                                "\t\"meat\": \"50\"\n" +
                                "}"))
                        .headers(h -> h
                                .set(HttpHeaderNames.CONTENT_TYPE, "application/json")
                                .set("token", TOKEN)
                        )
                )
                .put("/refrigerator/1/item");

        assertEquals("{\"id\":\"1\",\"items\":{\"pizza\":12,\"meat\":12,\"soda\":12}}", response.getBody().getText());
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void delete_refrigerator1_item() {
        ReceivedResponse response = httpClient
                .requestSpec(r -> r
                        .body(b -> b.text("{\n" +
                                "\t\"soda\": \"2\",\n" +
                                "\t\"pizza\": \"2\",\n" +
                                "\t\"meat\": \"5\"\n" +
                                "}"))
                        .headers(h -> h
                                .set(HttpHeaderNames.CONTENT_TYPE, "application/json")
                                .set("token", TOKEN)
                        )
                )
                .delete("/refrigerator/1/item");

        assertEquals("{\"id\":\"1\",\"items\":{\"soda\":1}}", response.getBody().getText());
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void post_refrigerator4_item_notExist() {
        ReceivedResponse response = httpClient
                .requestSpec(r -> r
                        .body(b -> b.text("{\n" +
                                "\t\"soda\": \"2\",\n" +
                                "\t\"pizza\": \"2\",\n" +
                                "\t\"meat\": \"5\"\n" +
                                "}"))
                        .headers(h -> h
                                .set(HttpHeaderNames.CONTENT_TYPE, "application/json")
                                .set("token", TOKEN)
                        )
                )
                .post("/refrigerator/4/item");

        assertEquals("Client error 404", response.getBody().getText());
        assertEquals(404, response.getStatusCode());
    }

    @Test
    public void put_refrigerator4_item_notExist() {
        ReceivedResponse response = httpClient
                .requestSpec(r -> r
                        .body(b -> b.text("{\n" +
                                "\t\"soda\": \"2\",\n" +
                                "\t\"pizza\": \"2\",\n" +
                                "\t\"meat\": \"5\"\n" +
                                "}"))
                        .headers(h -> h
                                .set(HttpHeaderNames.CONTENT_TYPE, "application/json")
                                .set("token", TOKEN)
                        )
                )
                .put("/refrigerator/4/item");

        assertEquals("Client error 404", response.getBody().getText());
        assertEquals(404, response.getStatusCode());
    }

    @Test
    public void delete_refrigerator4_item_notExist() {
        ReceivedResponse response = httpClient
                .requestSpec(r -> r
                        .body(b -> b.text("{\n" +
                                "\t\"soda\": \"2\",\n" +
                                "\t\"pizza\": \"2\",\n" +
                                "\t\"meat\": \"5\"\n" +
                                "}"))
                        .headers(h -> h
                                .set(HttpHeaderNames.CONTENT_TYPE, "application/json")
                                .set("token", TOKEN)
                        )
                )
                .delete("/refrigerator/4/item");

        assertEquals("Client error 404", response.getBody().getText());
        assertEquals(404, response.getStatusCode());
    }

    @After
    public void shutdown() {
        appUnderTest.close();
    }

}
