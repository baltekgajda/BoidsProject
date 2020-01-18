import akka.actor.AbstractActor;
        import akka.actor.ActorRef;
        import akka.japi.pf.ReceiveBuilder;

public class Actor extends AbstractActor {

    @Override
    public Receive createReceive() {
        return new ReceiveBuilder()
                .match(String.class, m -> {
                    System.out.println("received!" + m.toString());
                })
                .build();
    }
}
