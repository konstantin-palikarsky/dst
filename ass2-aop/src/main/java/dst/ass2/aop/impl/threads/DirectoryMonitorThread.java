package dst.ass2.aop.impl.threads;

import dst.ass2.aop.IPluginExecutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DirectoryMonitorThread extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(PluginExecutorThread.class);

    private final File dir;
    private final ExecutorService pluginExecutor;
    private final WatchService watchService;

    private final Set<byte[]> executedPlugins = new HashSet<>();


    public DirectoryMonitorThread(File dir, ExecutorService pluginExecutor) {
        this.dir = dir;
        this.pluginExecutor = pluginExecutor;

        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create watch service for directory: " + dir.getName(), e);
        }
    }

    @Override
    public void run() {
        LOG.info("Attempted to monitor directory {}", dir.getName());
        handleInitialPlugins();

        WatchKey directoryKey;
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

    private void handleInitialPlugins() {
        var files = Objects.requireNonNull(dir.listFiles());
        if (files.length == 0) {
            return;
        }

        Arrays.stream(files).forEach(this::handleFile);
    }

    private File eventToFile(WatchEvent<?> event) {
        var filePath = dir.toPath().resolve(((Path) event.context()));

        return filePath.toAbsolutePath().toFile();
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

        pluginsToExecute.stream()
                .map(PluginExecutorThread::new)
                .forEach(pluginExecutor::execute);

        executedPlugins.add(hashFile(file));
    }

    private List<IPluginExecutable> getExecutablePlugins(File file) {
        List<IPluginExecutable> returnList = new ArrayList<>();

        try (ZipInputStream zip = new ZipInputStream(new FileInputStream(file))) {
            ZipEntry entry;

            while ((entry = zip.getNextEntry()) != null) {

                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    var className = formatClassName(entry.getName());
                    var plugin = createPlugin(file, className);

                    if (plugin != null) {
                        returnList.add(plugin);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Exception while parsing jar: " + file.getName(), e);
        }

        return returnList;
    }

    private String formatClassName(String entryName) {
        return entryName
                .replace('/', '.')
                .substring(0, entryName.lastIndexOf(".class"));
    }

    private IPluginExecutable createPlugin(File file, String className) {
        try (URLClassLoader cl = new URLClassLoader(new URL[]{file.toURI().toURL()})) {
            var loadedClass = cl.loadClass(className);

            if (IPluginExecutable.class.isAssignableFrom(loadedClass)) {
                return (IPluginExecutable) loadedClass.getDeclaredConstructor().newInstance();
            }

        } catch (Exception ignored) {
        }

        return null;
    }

    private byte[] hashFile(File file) {

        try {
            byte[] data = Files.readAllBytes(file.toPath());
            return MessageDigest.getInstance("MD5").digest(data);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Catastrophic failure while hashing file in the MonitorThread", e);
        }

    }

}
