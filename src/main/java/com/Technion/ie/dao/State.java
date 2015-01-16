package com.Technion.ie.dao;

public enum State {
	
	O("O", 0),
	I_GENE("I-GENE", 1);
    
	
    private String name;
    private int id;
    
    State(String name, int id) {

        this.name = name;
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public int getId() {
        return id;
    }
    
    public static State getStateFromId(int id) {
        switch (id) {
            case 0:
                return O;

            case 1:
                return I_GENE;
        }
        
        throw new IllegalArgumentException("Unrecognised state id : " + id);
    }
        
        public static int getStateSize() { return 2; }
	
}
