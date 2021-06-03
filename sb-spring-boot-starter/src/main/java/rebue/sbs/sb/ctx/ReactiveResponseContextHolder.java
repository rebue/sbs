package rebue.sbs.sb.ctx;

import org.springframework.http.server.reactive.ServerHttpResponse;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public class ReactiveResponseContextHolder {
    static final Class<ServerHttpResponse> CONTEXT_KEY = ServerHttpResponse.class;

    /**
     * Gets the {@code Mono<ServerHttpRequest>} from Reactor {@link Context}
     *
     * @return the {@code Mono<ServerHttpRequest>}
     */
    public static Mono<ServerHttpResponse> getResponse() {
        return Mono.subscriberContext().map(ctx -> ctx.get(CONTEXT_KEY));
    }

}