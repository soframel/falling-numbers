package org.soframel.fallingnumbers.characteristics;

/**
 * User: sophie
 * Date: 1/3/14
 */
public enum Difficulty {
    veryEasy("0"), easy("1"), medium("2"), hard("3");

    private String code;
    private Difficulty(String code){
        this.code=code;
    }


    @Override
    public String toString() {
        return code;
    }

    public static Difficulty parseDifficulty(String s){
        if(s!=null){
            Difficulty[] diffs=Difficulty.values();
            for(Difficulty d: diffs){
                if(d.code.equals(s))
                    return d;
            }

        }
        return null;
    }
}
