package rebue.sbs.sb.ctx;

import org.springframework.http.server.reactive.ServerHttpRequest;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public class ReactiveRequestContextHolder {
    static final Class<ServerHttpRequest> CONTEXT_KEY = ServerHttpRequest.class;

    /**
     * Gets the {@code Mono<ServerHttpRequest>} from Reactor {@link Context}
     *
     * @return the {@code Mono<ServerHttpRequest>}
     */
    public static Mono<ServerHttpRequest> getRequest() {
        return Mono.subscriberContext().map(ctx -> ctx.get(CONTEXT_KEY));
    }

}