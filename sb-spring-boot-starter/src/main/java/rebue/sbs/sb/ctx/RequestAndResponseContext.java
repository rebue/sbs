package rebue.sbs.sb.ctx;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestAndResponseContext {
    private ServerHttpRequest  request;
    private ServerHttpResponse response;
}
