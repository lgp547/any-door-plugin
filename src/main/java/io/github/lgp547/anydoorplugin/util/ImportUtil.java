package io.github.lgp547.anydoorplugin.util;

import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import io.github.lgp547.anydoorplugin.settings.AnyDoorSettingsState;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.UnaryOperator;

public class ImportUtil {

    public static UnaryOperator<String> anyDoorJarPath = version -> "/io/github/lgp547/any-door/" + version + "/any-door-" + version + ".jar";

    public static void fillAnyDoorJar(Project project, @Nullable String runModuleName, String version) {
        try {
            Module module = getMainModule(project, Optional.ofNullable(runModuleName));
            String jarName = "any-door";
            removeModuleLibraryIfExist(module, jarName);
            File file = fillModuleLibrary(project, module, jarName + "-" + version, anyDoorJarPath.apply(version));
            NotifierUtil.notifyInfo(project, module.getName() + " fill ModuleLibrary success " + file.getPath());
        } catch (Exception e) {
            NotifierUtil.notifyError(project, "fill ModuleLibrary fail: " + e.getMessage());
        }

    }

    public static void fillAnyDoorJar(ExecutionEnvironment env) {
        Project project = env.getProject();
        try {
            // Setting
            AnyDoorSettingsState anyDoorSettingsState = AnyDoorSettingsState.getAnyDoorSettingsState(project);
            String version = anyDoorSettingsState.version;
            String runModule = anyDoorSettingsState.runModule;

            // module
            Module module;
            RunProfile runProfile = env.getRunProfile();
            if (runProfile instanceof ApplicationConfiguration) {
                module = ((ApplicationConfiguration) runProfile).getDefaultModule();
            } else {
                module = getMainModule(project, Optional.of(runModule));
                throw new IllegalStateException("not supported");
            }
            String jarName = "any-door";
            String libraryName = jarName + "-" + version;
            boolean isExist = checkModuleLibrary(module, jarName, libraryName);
            if (!isExist) {
                File file = fillModuleLibrary(project, module, libraryName, anyDoorJarPath.apply(version));
                NotifierUtil.notifyInfo(project, String.format("fill library success. module:%s path:%s", module.getName(), file.getPath()));
            }
        } catch (Exception e) {
            NotifierUtil.notifyError(project, "fill ModuleLibrary fail: " + e.getMessage());
        }

    }

    /**
     * @param project     current run project
     * @param module      main function module
     * @param libraryName import jar name
     * @param jarPath     import jar path
     */
    public static File fillModuleLibrary(Project project, Module module, String libraryName, String jarPath) throws IOException {
        File localRepository = MavenProjectsManager.getInstance(project).getLocalRepository();
        String localPath = localRepository.getPath() + jarPath;
        File file = new File(localPath);
        if (!file.isFile()) {
            String httpPath = "https://s01.oss.sonatype.org/content/repositories/releases" + jarPath;
            FileUtils.copyURLToFile(new URL(httpPath), file);
            if (!file.isFile()) {
                throw new RuntimeException("get jar file fail");
            }
        }
        ModuleRootModificationUtil.addModuleLibrary(module, libraryName, List.of("file://" + file.getPath()), List.of());
        return file;
    }

    public static Module getMainModule(Project project, Optional<String> runModuleNameOp) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        if (modules.length == 1) {
            return modules[0];
        }
        if (runModuleNameOp.isPresent()) {
            for (Module module : modules) {
                if (module.getName().equals(runModuleNameOp.get())) {
                    return module;
                }
            }
        }
        throw new RuntimeException("main module could not find. size " + modules.length);
    }

    public static void removeModuleLibraryIfExist(@NotNull Module module, String jarName) {
        ModuleRootModificationUtil.updateModel(module, modifiableRootModel -> {
            LibraryTable moduleLibraryTable = modifiableRootModel.getModuleLibraryTable();
            Iterator<Library> libraryIterator = moduleLibraryTable.getLibraryIterator();
            while (libraryIterator.hasNext()) {
                Library next = libraryIterator.next();
                if (StringUtils.contains(next.getName(), jarName)) {
                    moduleLibraryTable.removeLibrary(next);
                }
            }
        });
    }

    /**
     * if contains 'jarName' will remove
     *
     * @return if equals
     */
    public static boolean checkModuleLibrary(@NotNull Module module, String jarName, String libraryName) {
        AtomicBoolean isContains = new AtomicBoolean(false);
        ModuleRootModificationUtil.updateModel(module, modifiableRootModel -> {
            LibraryTable moduleLibraryTable = modifiableRootModel.getModuleLibraryTable();
            Iterator<Library> libraryIterator = moduleLibraryTable.getLibraryIterator();
            while (libraryIterator.hasNext()) {
                Library next = libraryIterator.next();
                if (StringUtils.equals(next.getName(), libraryName)) {
                    isContains.set(true);
                    return;
                } else if (StringUtils.contains(next.getName(), jarName)) {
                    moduleLibraryTable.removeLibrary(next);
                }
            }
        });
        return isContains.get();
    }
}
