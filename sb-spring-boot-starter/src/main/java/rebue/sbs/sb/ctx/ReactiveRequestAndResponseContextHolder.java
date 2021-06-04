package rebue.sbs.sb.ctx;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public class ReactiveRequestAndResponseContextHolder {
    static final String CONTEXT_KEY = "req.and.resp";

    /**
     * Gets the {@code Mono<ServerHttpRequest>} from Reactor {@link Context}
     *
     * @return the {@code Mono<ServerHttpRequest>}
     */
    public static Mono<RequestAndResponseContext> getRequestAndResponseContext() {
        return Mono.subscriberContext().map(ctx -> ctx.get(CONTEXT_KEY));
    }

}