package ui;

import service.Service;

public class AbstractUI {
    protected final Service service;

    public AbstractUI(Service service) {
        this.service = service;
    }
}
