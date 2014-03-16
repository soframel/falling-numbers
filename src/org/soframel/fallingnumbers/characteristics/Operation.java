package org.soframel.fallingnumbers.characteristics;

/**
 * User: sophie
 * Date: 1/3/14
 */
public enum Operation {
    Plus("+"), Minus("-");

    private String code;
    private Operation(String code){
         this.code=code;
    }

    @Override
    public String toString(){
          return this.code;
    }

    public static Operation parseOperation(String s){
        if(s!=null){
            Operation[] ops=Operation.values();
            for(Operation o: ops){
                 if(o.code.equals(s))
                     return o;
            }

        }
        return null;
    }
}
