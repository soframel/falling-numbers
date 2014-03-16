package org.soframel.fallingnumbers.characteristics;

/**
 * User: sophie
 * Date: 1/3/14
 */
public class GameSettings {

    private Operation operation;

    private Difficulty difficulty;

    public Operation getOperation() {
        return operation;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        return ((operation!=null)?operation.toString():"")+";"+((difficulty!=null)?difficulty.toString():"");
    }

    public static GameSettings parseSettings(String settings){
        GameSettings gs=null;
        int index=settings.indexOf(';');
        if(index>0 && settings.length()>index+1){
            gs=new GameSettings();

            String opS=settings.substring(0, index);
            Operation op=Operation.parseOperation(opS);
            gs.setOperation(op);

            String diffS=settings.substring(index+1);
            Difficulty diff=Difficulty.parseDifficulty(diffS);
            gs.setDifficulty(diff);
        }
        return gs;
    }
}
