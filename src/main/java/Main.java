import org.apache.log4j.BasicConfigurator;

/**
 * Created by mblaszkiewicz on 14.01.2017.
 */

public class Main {

    public static void main(String[] args) {
        BasicConfigurator.configure();
        Messenger messenger = new Messenger();
    }
}
