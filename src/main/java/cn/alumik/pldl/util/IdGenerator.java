package cn.alumik.pldl.util;

public class IdGenerator {

    private static final char START_CODE = 'A';

    private static final char END_CODE = 'Z';

    private static final StringBuilder id = new StringBuilder();

    static {
        id.append(START_CODE);
    }

    public static String next() {
        StringBuilder result = new StringBuilder(id);
        boolean finish = false;
        for (int i = 0; i < id.length(); i++) {
            if (id.charAt(i) != END_CODE) {
                id.setCharAt(i, (char) (id.charAt(i) + 1));
                finish = true;
                break;
            } else {
                id.setCharAt(i, START_CODE);
            }
        }
        if (!finish) {
            id.append(START_CODE);
        }
        return result.reverse().toString();
    }
}
