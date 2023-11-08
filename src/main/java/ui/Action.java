package ui;

import java.io.IOException;

@FunctionalInterface
public interface Action {
    void performAction() throws IOException;
}
