package pl.edu.pw.elka.enums;

public enum Roads {
	A("A"), B("B"), C("C"), D("D");
	public final String state;

	Roads(final String s) {
		this.state = s;
	}

	public static Roads getByIndex(int road) {
		switch (road) {
			case 1:
				return Roads.A;
			case 0:
				return Roads.B;
			case 2:
				return Roads.C;
			case 3:
				return Roads.D;
			default:
				return null;
		}
	}

	public static int indexOf(String road) {
		switch (road) {
			case "A":// Reverted values of A and B to sum up opposite roads to 3
				return 1;
			case "B":
				return 0;
			case "C":
				return 2;
			case "D":
				return 3;
		}
		throw new RuntimeException("Invalid road value");
	}

	public static String getByOrderedIndex(int road) {
		switch (road) {
			case 0:
				return Roads.A.state;
			case 1:
				return Roads.B.state;
			case 2:
				return Roads.C.state;
			case 3:
				return Roads.D.state;
			default:
				return "";
		}
	}

	public static int orderedIndexOf(String road) {
		switch (road) {
			case "A":// Reverted values of A and B to sum up opposite roads to 3
				return 0;
			case "B":
				return 1;
			case "C":
				return 2;
			case "D":
				return 3;
		}
		throw new RuntimeException("Invalid road value");
	}

	public int getIndex() {
		return Roads.indexOf(this.state);
	}

	public int getOrderedIndex() {
		return Roads.orderedIndexOf(this.state);
	}
}
