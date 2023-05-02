package dst.ass2.aop.impl.threads;

import dst.ass2.aop.IPluginExecutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DirectoryMonitorThread extends Thread {
    private final File dir;
    private final ExecutorService pluginRunnerPool;
    private final WatchService watchService;
    private WatchKey directoryKey;

    private final Set<byte[]> executedPlugins = new HashSet<>();
    private static final Logger LOG = LoggerFactory.getLogger(PluginExecutorThread.class);


    public DirectoryMonitorThread(File dir, ExecutorService pluginRunnerPool) {
        this.dir = dir;
        this.pluginRunnerPool = pluginRunnerPool;

        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create watch service for directory: " + dir.getName());
        }
    }

    @Override
    public void run() {
        LOG.info("Attempted to monitor directory {}", dir.getName());
        handleInitialPlugins();

        try {
            directoryKey = dir.toPath().register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't register watch service on dir: " + dir.getName());
        }

        try {
            WatchKey key;
            while ((key = watchService.take()) != null && !currentThread().isInterrupted()) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    handleFile(eventToFile(event));
                }
                key.reset();
            }
        } catch (InterruptedException ignored) {
        } finally {
            LOG.info("Exiting monitor for {}", dir.getName());
            directoryKey.cancel();
        }
    }


    private void handleFile(File file) {

        if (!file.exists() || !file.getName().endsWith(".jar") ||
                executedPlugins.contains(hashFile(file))) {
            return;
        }
        var pluginsToExecute = getExecutablePlugins(file);
        if (pluginsToExecute.isEmpty()) {
            return;
        }
        pluginsToExecute.forEach(x -> pluginRunnerPool.execute(new PluginExecutorThread(x)));
        executedPlugins.add(hashFile(file));
    }

    private List<IPluginExecutable> getExecutablePlugins(File file) {
        var returnList = new ArrayList<IPluginExecutable>();
        try {
            ZipInputStream zip = new ZipInputStream(new FileInputStream(file.getAbsoluteFile()));
            for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {

                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    String className = entry.getName().replace('/', '.');
                    className = className.substring(0, className.length() - ".class".length());

                    try (URLClassLoader cl = new URLClassLoader(new URL[]{file.toURI().toURL()})) {

                        var constructors = cl.loadClass(className).getDeclaredConstructors();
                        var defaultConstructor = Arrays.stream(constructors).filter(x ->
                                x.getParameterCount() == 0).collect(Collectors.toList()).get(0);

                        defaultConstructor.setAccessible(true);
                        returnList.add((IPluginExecutable) defaultConstructor.newInstance());


                    } catch (IllegalAccessException | InstantiationException | ClassNotFoundException |
                             ClassCastException | InvocationTargetException ignored) {
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Catastrophal exception while parsing jar: " + file.getName());
        }
        return returnList;
    }

    private File eventToFile(WatchEvent<?> event) {
        var filePath = dir.toPath().resolve(((Path) event.context()));

        return filePath.toAbsolutePath().toFile();
    }

    private void handleInitialPlugins() {
        var files = Objects.requireNonNull(dir.listFiles());
        if (files.length == 0) {
            return;
        }

        Arrays.stream(files).forEach(this::handleFile);
    }

    private byte[] hashFile(File file) {

        try {
            byte[] data = Files.readAllBytes(file.toPath());
            return MessageDigest.getInstance("MD5").digest(data);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

}
