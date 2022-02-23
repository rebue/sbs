package rebue.sbs.aop;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties("rebue.sbs.aop")
public class AopProperties {

    private Ctrl ctrl = new Ctrl();

    private Sub  sub  = new Sub();

    private Api  api  = new Api();

    private Svc  svc  = new Svc();

    @Data
    class Ctrl {
        private Enabled log = new Enabled();
        private Enabled err = new Enabled();
    }

    @Data
    class Sub {
        private Enabled log = new Enabled();
        private Enabled err = new Enabled();
    }

    @Data
    class Api {
        private Enabled log = new Enabled();
        private Enabled err = new Enabled();
    }

    @Data
    class Svc {
        private Enabled log = new Enabled();
        private Enabled err = new Enabled();
    }

    @Data
    class Enabled {
        private Boolean enabled = true;
    }
}
