package boids.model.messages;

public class MessageGenerateBoids {

    int amountToGenerate;

    public MessageGenerateBoids(int amountToGenerate) {
        this.amountToGenerate = amountToGenerate;
    }

    public int getAmountToGenerate() {
        return amountToGenerate;
    }
}
