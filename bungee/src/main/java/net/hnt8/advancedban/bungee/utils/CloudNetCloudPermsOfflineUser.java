package net.hnt8.advancedban.bungee.utils;

import net.hnt8.advancedban.utils.Permissionable;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;

import java.util.List;

public class CloudNetCloudPermsOfflineUser implements Permissionable {
    private IPermissionUser permissionUser;

    public CloudNetCloudPermsOfflineUser(String name) {
        final List <IPermissionUser> users = CloudNetDriver.getInstance().getPermissionManagement().getUsers(name);

        if (!users.isEmpty()) {
            this.permissionUser = users.get(0);
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        return permissionUser != null && permissionUser.hasPermission(permission).asBoolean();
    }
}
