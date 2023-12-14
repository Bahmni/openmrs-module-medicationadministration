//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.openmrs.util;

import org.openmrs.annotation.AddOnStartup;
import org.openmrs.annotation.HasAddOnStartupPrivileges;

@HasAddOnStartupPrivileges
public class PrivilegeConstants {
    @AddOnStartup(description = "Able to get Medication Administrations")
    public static final String GET_MEDICATION_ADMINISTRATIONS = "Get Medication Administrations";

    @AddOnStartup(description = "Able to edit Medication Administration")
    public static final String EDIT_MEDICATION_ADMINISTRATION = "Edit Medication Administration";

    @AddOnStartup(description = "Able to delete Medication Administration")
    public static final String DELETE_MEDICATION_ADMINISTRATION = "Delete Medication Administration";
    private PrivilegeConstants() {
    }
}
