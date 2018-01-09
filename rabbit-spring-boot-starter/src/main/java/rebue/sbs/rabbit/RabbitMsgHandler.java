package rebue.sbs.rabbit;

@FunctionalInterface
public interface RabbitMsgHandler<T> {
    void handle(T msg);
}
