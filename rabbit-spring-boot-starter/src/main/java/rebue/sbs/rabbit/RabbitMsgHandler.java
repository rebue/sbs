package rebue.sbs.rabbit;

@FunctionalInterface
public interface RabbitMsgHandler<T> {
    boolean handle(T msg);
}
