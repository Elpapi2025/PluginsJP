package com.ejemplo.economiaplugin.models;

public class Moneda {
    private String id;
    private String name;
    private String symbol;
    private String commandAlias;
    private double initialBalance;
    private boolean isDefault;
    private int decimalPlaces;
    private String permissionCheck;
    private String permissionPay;
    private String permissionAdmin;
    private boolean requiresPermission;

    public Moneda(String id, String name, String symbol, String commandAlias, double initialBalance, boolean isDefault, int decimalPlaces,
                  String permissionCheck, String permissionPay, String permissionAdmin, boolean requiresPermission) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.commandAlias = commandAlias;
        this.initialBalance = initialBalance;
        this.isDefault = isDefault;
        this.decimalPlaces = decimalPlaces;
        this.permissionCheck = permissionCheck;
        this.permissionPay = permissionPay;
        this.permissionAdmin = permissionAdmin;
        this.requiresPermission = requiresPermission;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCommandAlias() {
        return commandAlias;
    }

    public double getInitialBalance() {
        return initialBalance;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public String getPermissionCheck() {
        return permissionCheck;
    }

    public String getPermissionPay() {
        return permissionPay;
    }

    public String getPermissionAdmin() {
        return permissionAdmin;
    }

    public boolean requiresPermission() {
        return requiresPermission;
    }
}