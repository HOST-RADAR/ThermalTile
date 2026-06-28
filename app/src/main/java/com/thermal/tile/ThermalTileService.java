package com.thermal.tile;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class ThermalTileService extends TileService {

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateTile();
    }

    @Override
    public void onClick() {
        super.onClick();
        boolean isEnabled = isThermalRunning();
        if (isEnabled) {
            runCommand("stop mi_thermald && stop vendor.thermal-engine");
        } else {
            runCommand("start mi_thermald && start vendor.thermal-engine");
        }
        try { Thread.sleep(800); } catch (InterruptedException e) { e.printStackTrace(); }
        updateTile();
    }

    private boolean isThermalRunning() {
        String result = runCommandOutput("getprop init.svc.mi_thermald");
        return result.trim().equals("running");
    }

    private void updateTile() {
        Tile tile = getQsTile();
        if (tile == null) return;
        boolean running = isThermalRunning();
        tile.setState(running ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.setSubtitle(running ? "Enabled" : "Disabled");
        tile.updateTile();
    }

    private void runCommand(String cmd) {
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String runCommandOutput(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});
            byte[] bytes = p.getInputStream().readAllBytes();
            return new String(bytes).trim();
        } catch (Exception e) {
            return "";
        }
    }
}
