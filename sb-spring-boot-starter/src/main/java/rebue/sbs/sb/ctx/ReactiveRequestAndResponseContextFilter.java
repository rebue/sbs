package rebue.sbs.sb.ctx;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveRequestAndResponseContextFilter implements WebFilter, Ordered {

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final WebFilterChain chain) {
        final ServerHttpResponse        response = exchange.getResponse();
        final ServerHttpRequest         request  = exchange.getRequest();
        final RequestAndResponseContext context  = new RequestAndResponseContext(request, response);
        return chain.filter(exchange).subscriberContext(ctx -> ctx.put(ReactiveRequestAndResponseContextHolder.CONTEXT_KEY, context));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}