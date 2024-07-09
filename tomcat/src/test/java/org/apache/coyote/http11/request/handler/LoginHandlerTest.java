package org.apache.coyote.http11.request.handler;

import org.apache.coyote.http11.HttpRequestHeaderParser;
import org.apache.coyote.http11.HttpRequestParser;
import org.apache.coyote.http11.model.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LoginHandlerTest {

    @DisplayName("로그인 요청시 requestBody가 없는 경우 화면을 출력한다.")
    @Test
    void loginQueryParamEmptyTest() throws IOException {
        // given
        final List<String> request = List.of(
                "GET /login.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final HttpRequest httpRequest = HttpRequestParser.getInstance()
                .parse(HttpRequestHeaderParser.getInstance().parse(request), "");

        final RequestHandler loginHandler = new LoginHandler();
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 3447 ",
                "",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath())));

        // when
        final String response = loginHandler.handle(httpRequest);

        // then
        assertThat(response).isEqualTo(expected);
    }

    @DisplayName("로그인 요청시 requestBody가 있고, 인증된 유저의 경우 index로 리다이렉트 한다.")
    @Test
    void loginSuccessTest() throws IOException {
        // given
        final List<String> request = List.of(
                "POST /login.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 31 ",
                "",
                "account=gugu&password=password");

        final HttpRequest httpRequest = HttpRequestParser.getInstance()
                .parse(HttpRequestHeaderParser.getInstance().parse(request), "account=gugu&password=password");

        final RequestHandler loginHandler = new LoginHandler();
        var expected = String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: /index.html ",
                "",
                "");

        // when
        final String response = loginHandler.handle(httpRequest);

        // then
        assertThat(response).isEqualTo(expected);
    }

    @DisplayName("로그인 요청시 requestBody가 있고, 인증이 안된 유저의 경우 401로 리다이렉트 한다.")
    @Test
    void loginFailTest() throws IOException {
        // given
        final List<String> request = List.of(
                "POST /login.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 32 ",
                "",
                "account=gugu&password=password1");

        final HttpRequest httpRequest = HttpRequestParser.getInstance()
                .parse(HttpRequestHeaderParser.getInstance().parse(request), "account=gugu&password=password1");

        final RequestHandler loginHandler = new LoginHandler();
        var expected = String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: /401.html ",
                "",
                "");

        // when
        final String response = loginHandler.handle(httpRequest);

        // then
        assertThat(response).isEqualTo(expected);
    }
}
