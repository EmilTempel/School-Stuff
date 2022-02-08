package assembly;

import java.util.ArrayList;

import assembly.Command.Type;

public class RegisterMachine {

	int BZ, A;
	int[] R;

	boolean running;

	public RegisterMachine() {
		BZ = 0;
		A = 0;
		R = new int[0];
		running = true;
	}

	public RegisterMachine(RegisterMachine rm) {
		BZ = rm.BZ;
		A = rm.A;
		R = rm.R.clone();
		running = rm.running;
	}

	public static ArrayList<RegisterMachine> run(RegisterMachine rm,
			Command[] instructions) {
		ArrayList<RegisterMachine> steps = new ArrayList<RegisterMachine>();
		steps.add(new RegisterMachine(rm));

		int i = 0;
		while (rm.running) {
			instructions[rm.BZ].execute(rm);
			steps.add(new RegisterMachine(rm));
		}

		return steps;
	}

	public static Command[] fromString(String str) {
		String[] split = str.split("\n");
		Command[] instructions = new Command[split.length];

		for (int i = 0; i < split.length; i++) {
			String[] cmd = split[i].split(" ");
			instructions[i] = new Command(Command.Type.fromString(cmd[0]),Integer.parseInt(cmd[1]));
		}

		return instructions;
	}
	
	public int getBZ() {
		return BZ;
	}
	
	public int getA() {
		return A;
	}
	
	public int[] getR() {
		return R;
	}

	public int R(int x) {
		if (x > R.length-1) {
			return 0;
		} else {
			return R[x];
		}
	}

	public void setR(int x, int val) {
		if (x > R.length-1) {
			int[] temp = new int[x+1];
			for (int i = 0; i < R.length; i++) {
				temp[i] = R[i];
			}
			R = temp;
		}
		R[x] = val;
	}

	public void LOAD(int x) {
		A = R(x);
		BZ++;
	}

	public void DLOAD(int x) {
		A = x;
		BZ++;
	}

	public void STORE(int x) {
		setR(x, A);
		BZ++;
	}

	public void ADD(int x) {
		A += R(x);
		BZ++;
	}

	public void SUB(int x) {
		A -= R(x);
		BZ++;
	}

	public void MULT(int x) {
		A *= R(x);
		BZ++;
	}

	public void DIV(int x) {
		A /= R(x);
		BZ++;
	}

	public void JUMP(int n) {
		BZ = n;
	}

	public void JGE(int n) {
		if (A >= 0) {
			JUMP(n);
		} else {
			BZ++;
		}
	}

	public void JGT(int n) {
		if (A > 0) {
			JUMP(n);
		} else {
			BZ++;
		}
	}

	public void JLE(int n) {
		if (A <= 0) {
			JUMP(n);
		} else {
			BZ++;
		}
	}

	public void JLT(int n) {
		if (A >= 0) {
			JUMP(n);
		} else {
			BZ++;
		}
	}

	public void JEQ(int n) {
		if (A == 0) {
			JUMP(n);
		} else {
			BZ++;
		}
	}

	public void JNE(int n) {
		if (A != 0) {
			JUMP(n);
		} else {
			BZ++;
		}
	}

	public void END() {
		running = false;
		BZ++;
	}

	public String toString() {
		String str = BZ + " | " + A + " | ";
		for (int i = 0; i < R.length; i++) {
			str += R[i];
			if (i < R.length - 1)
				str += " | ";
		}
		return str;
	}

	public static void main(String[] args) {
		Command[] instructions = { new Command(Type.LOAD, 1),
				new Command(Type.MULT, 2), new Command(Type.MULT, 3),
				new Command(Type.END, 0) };
		RegisterMachine rm = new RegisterMachine();
		rm.R = new int[] { 2, 2, 2 };

		System.out.println(run(rm, instructions));
	}

}
