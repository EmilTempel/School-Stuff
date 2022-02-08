package assembly;

public class Command {

	Type type;
	int x;

	public Command(Type type, int x) {
		this.type = type;
		this.x = x;
	}

	public void execute(RegisterMachine rm) {
		type.execute(rm, x);
	}
	
	public String toString() {
		return type + " " + x;
	}

	public enum Type {
		LOAD((rm, x) -> rm.LOAD(x)), DLOAD((rm, x) -> rm.DLOAD(x)), STORE((rm,
				x) -> rm.STORE(x)), ADD((rm, x) -> rm.ADD(x)), SUB(
				(rm, x) -> rm.SUB(x)), MULT((rm, x) -> rm.MULT(x)), DIV(
				(rm, x) -> rm.DIV(x)), JUMP((rm, x) -> rm.JUMP(x)), JGE(
				(rm, x) -> rm.JGE(x)), JGT((rm, x) -> rm.JGT(x)), JLE(
				(rm, x) -> rm.JLE(x)), JLT((rm, x) -> rm.JLT(x)), JEQ(
				(rm, x) -> rm.JEQ(x)), JNE((rm, x) -> rm.JNE(x)), END(
				(rm, x) -> rm.END());

		Cmd cmd;

		Type(Cmd cmd) {
			this.cmd = cmd;
		}

		public void execute(RegisterMachine rm, int x) {
			cmd.execute(rm, x);
		}

		public static Type fromString(String str) {
			for (Type t : values()) {
				if (t.toString().equals(str))
					return t;
			}
			return null;
		}

		public static Type getMostFitting(String str) {
			Type type = values()[0];
			for (int i = 1; i < values().length; i++) {
				if (Math.abs(values()[i].toString().compareToIgnoreCase(str)) < Math
						.abs(type.toString().compareToIgnoreCase(str))) {
					type = values()[i];
				}
			}
			return type;
		}
	}

	interface Cmd {
		public abstract void execute(RegisterMachine rm, int x);
	}

	public static void main(String[] args) {
		System.out.println(Type.getMostFitting("LOA"));
	}
}
