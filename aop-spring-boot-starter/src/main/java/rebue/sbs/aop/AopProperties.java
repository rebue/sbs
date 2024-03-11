package rebue.sbs.aop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("rebue.sbs.aop")
public class AopProperties {

    private Ctrl ctrl = new Ctrl();

    private Sub sub = new Sub();

    private Api api = new Api();

    private Svc svc = new Svc();

    @Data
    static class Ctrl {
        private Enabled log = new Enabled();
        private Enabled err = new Enabled();
    }

    @Data
    static class Sub {
        private Enabled log = new Enabled();
        private Enabled err = new Enabled();
    }

    @Data
    static class Api {
        private Enabled log = new Enabled();
        private Enabled err = new Enabled();
    }

    @Data
    static class Svc {
        private Enabled log = new Enabled();
        private Enabled err = new Enabled();
    }

    @Data
    static class Enabled {
        private Boolean enabled = true;
    }
}
