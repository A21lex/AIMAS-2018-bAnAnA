package aimas; /**
 * This class has been borrowed from the Warm-up Assignment in order to represent
 * possible commands which could be taken by an agent - thanks to Thomas Bolander!
 */
import java.util.LinkedList;

public class Command {
    // Order of enum important for determining opposites.
    public static enum Dir {
        N, W, E, S
    };

    public static enum CommandType {
        Move, Push, Pull, NoOp
    };

    public static final Command[] EVERY;
    static {
        LinkedList<Command> cmds = new LinkedList<Command>();
        for (Dir d1 : Dir.values()) {
            for (Dir d2 : Dir.values()) {
                if (!Command.isOpposite(d1, d2)) {
                    cmds.add(new Command(CommandType.Push, d1, d2));
                }
            }
        }
        for (Dir d1 : Dir.values()) {
            for (Dir d2 : Dir.values()) {
                if (d1 != d2) {
                    cmds.add(new Command(CommandType.Pull, d1, d2));
                }
            }
        }
        for (Dir d : Dir.values()) {
            cmds.add(new Command(d));
        }

        EVERY = cmds.toArray(new Command[0]);
    }

    public static boolean isOpposite(Dir d1, Dir d2) {
        return d1.ordinal() + d2.ordinal() == 3;
    }

    public static int dirToRowChange(Dir d) {
        // South is down one row (1), north is up one row (-1).
        switch (d) {
            case S:
                return 1;
            case N:
                return -1;
            default:
                return 0;
        }
    }

    public static int dirToColChange(Dir d) {
        // East is right one column (1), west is left one column (-1).
        switch (d) {
            case E:
                return 1;
            case W:
                return -1;
            default:
                return 0;
        }
    }

    public final CommandType actionCommandType;
    public final Dir dir1;
    public final Dir dir2;

    public Command(Dir d) {
        this.actionCommandType = CommandType.Move;
        this.dir1 = d;
        this.dir2 = null;
    }

    public Command(CommandType t, Dir d1, Dir d2) {
        this.actionCommandType = t;
        this.dir1 = d1;
        this.dir2 = d2;
    }

    public Command(CommandType t){
        this.actionCommandType = t;
        this.dir1 = null;
        this.dir2 = null;
    }
    public static Command getOppositeMoveCommand(Command c){
        if (c.actionCommandType == CommandType.Move){
            if (c.dir1 == Dir.N){
                return new Command(Dir.S);
            }
            else if (c.dir1 == Dir.S){
                return new Command(Dir.N);
            }
            else if(c.dir1 == Dir.W){
                return new Command(Dir.E);
            }
            else {
                return new Command(Dir.W);
            }
        }
        else {
            return new Command(CommandType.NoOp);
        }
    }
    @Override
    public String toString() {
        if (this.actionCommandType == CommandType.Move)
            return String.format("[%s(%s)]", this.actionCommandType.toString(), this.dir1.toString());
        else if (this.actionCommandType == CommandType.Push || this.actionCommandType == CommandType.Pull)
            return String.format("[%s(%s,%s)]", this.actionCommandType.toString(), this.dir1.toString(), this.dir2.toString());
        else
            return "[NoOp]";
    }
}