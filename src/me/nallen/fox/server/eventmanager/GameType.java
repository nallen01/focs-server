package me.nallen.fox.server.eventmanager;

public enum GameType {
    BASIC(0),
    TOSS_UP(1),
    SKYRISE(2),
    MISSILE_MANIA(3),
    NOTHING_BUT_NET(4),
    KNOT_A_PROBLEM(5),
    STARSTRUCK(6),
    IN_THE_ZONE(7),
    RINGMASTER(8);

    private int id;

    GameType(int id) {
        this.id = id;
    }

    public String getName() {
        switch(this) {
            case BASIC: return "Basic";
            case TOSS_UP: return "Toss Up";
            case SKYRISE: return "Skyrise";
            case MISSILE_MANIA: return "Missile Mania!";
            case NOTHING_BUT_NET: return "Nothing But Net";
            case KNOT_A_PROBLEM: return "Knot A Problem";
            case STARSTRUCK: return "Starstruck";
            case IN_THE_ZONE: return "In The Zone";
            case RINGMASTER: return "Ringmaster";
        }

        return "";
    }

    public Program getProgram() {
        switch(this) {
            case BASIC: return Program.VRC;
            case TOSS_UP: return Program.VRC;
            case SKYRISE: return Program.VRC;
            case MISSILE_MANIA: return Program.VRC;
            case NOTHING_BUT_NET: return Program.VRC;
            case KNOT_A_PROBLEM: return Program.VRC;
            case STARSTRUCK: return Program.VRC;
            case IN_THE_ZONE: return Program.VRC;
            case RINGMASTER: return Program.VIQC;
        }

        return Program.VRC;
    }

    public static GameType createFromServerIdentifier(String identifier) {
        switch(identifier) {
            case "Toss Up (VRC)": return GameType.TOSS_UP;
            case "Skyrise (VRC)": return GameType.SKYRISE;
            case "Nothing But Net (VRC)": return GameType.NOTHING_BUT_NET;
            case "Starstruck (VRC)": return GameType.STARSTRUCK;
            case "In The Zone (VRC)": return GameType.IN_THE_ZONE;
            case "Ringmaster (VEXIQ)": return GameType.RINGMASTER;
        }

        return null;
    }
}
