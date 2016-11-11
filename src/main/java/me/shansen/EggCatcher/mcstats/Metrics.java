/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Server
 *  org.bukkit.configuration.InvalidConfigurationException
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.configuration.file.YamlConfigurationOptions
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginDescriptionFile
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package me.shansen.EggCatcher.mcstats;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class Metrics {
    private static final int REVISION = 7;
    private static final String BASE_URL = "http://report.mcstats.org";
    private static final String REPORT_URL = "/plugin/%s";
    private static final int PING_INTERVAL = 15;
    private final Plugin plugin;
    private final Set<Graph> graphs = Collections.synchronizedSet(new HashSet());
    private final YamlConfiguration configuration;
    private final File configurationFile;
    private final String guid;
    private final boolean debug;
    private final Object optOutLock = new Object();
    private volatile BukkitTask task = null;

    public Metrics(Plugin plugin) throws IOException {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        this.plugin = plugin;
        this.configurationFile = this.getConfigFile();
        this.configuration = YamlConfiguration.loadConfiguration((File)this.configurationFile);
        this.configuration.addDefault("opt-out", (Object)false);
        this.configuration.addDefault("guid", (Object)UUID.randomUUID().toString());
        this.configuration.addDefault("debug", (Object)false);
        if (this.configuration.get("guid", (Object)null) == null) {
            this.configuration.options().header("http://mcstats.org").copyDefaults(true);
            this.configuration.save(this.configurationFile);
        }
        this.guid = this.configuration.getString("guid");
        this.debug = this.configuration.getBoolean("debug", false);
    }

    public Graph createGraph(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Graph name cannot be null");
        }
        Graph graph = new Graph(name);
        this.graphs.add(graph);
        return graph;
    }

    public void addGraph(Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph cannot be null");
        }
        this.graphs.add(graph);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean start() {
        Object object = this.optOutLock;
        synchronized (object) {
            if (this.isOptOut()) {
                return false;
            }
            if (this.task != null) {
                return true;
            }
            this.task = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new Runnable(){
                private boolean firstPost;

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public void run() {
                    block7 : {
                        try {
                            Object object = Metrics.this.optOutLock;
                            synchronized (object) {
                                if (Metrics.this.isOptOut() && Metrics.this.task != null) {
                                    Metrics.this.task.cancel();
                                    Metrics.this.task = null;
                                    for (Graph graph : Metrics.this.graphs) {
                                        graph.onOptOut();
                                    }
                                }
                            }
                            Metrics.this.postPlugin(!this.firstPost);
                            this.firstPost = false;
                        }
                        catch (IOException e) {
                            if (!Metrics.this.debug) break block7;
                            Bukkit.getLogger().log(Level.INFO, "[Metrics] " + e.getMessage());
                        }
                    }
                }
            }, 0, 18000);
            return true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isOptOut() {
        Object object = this.optOutLock;
        synchronized (object) {
            try {
                this.configuration.load(this.getConfigFile());
            }
            catch (IOException ex) {
                if (this.debug) {
                    Bukkit.getLogger().log(Level.INFO, "[Metrics] " + ex.getMessage());
                }
                return true;
            }
            catch (InvalidConfigurationException ex) {
                if (this.debug) {
                    Bukkit.getLogger().log(Level.INFO, "[Metrics] " + ex.getMessage());
                }
                return true;
            }
            return this.configuration.getBoolean("opt-out", false);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void enable() throws IOException {
        Object object = this.optOutLock;
        synchronized (object) {
            if (this.isOptOut()) {
                this.configuration.set("opt-out", (Object)false);
                this.configuration.save(this.configurationFile);
            }
            if (this.task == null) {
                this.start();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void disable() throws IOException {
        Object object = this.optOutLock;
        synchronized (object) {
            if (!this.isOptOut()) {
                this.configuration.set("opt-out", (Object)true);
                this.configuration.save(this.configurationFile);
            }
            if (this.task != null) {
                this.task.cancel();
                this.task = null;
            }
        }
    }

    public File getConfigFile() {
        File pluginsFolder = this.plugin.getDataFolder().getParentFile();
        return new File(new File(pluginsFolder, "PluginMetrics"), "config.yml");
    }

    private int getOnlinePlayers() {
        try {
            Method onlinePlayerMethod = Server.class.getMethod("getOnlinePlayers", new Class[0]);
            if (onlinePlayerMethod.getReturnType().equals(Collection.class)) {
                return ((Collection)onlinePlayerMethod.invoke((Object)Bukkit.getServer(), new Object[0])).size();
            }
            return ((Player[])onlinePlayerMethod.invoke((Object)Bukkit.getServer(), new Object[0])).length;
        }
        catch (Exception ex) {
            if (this.debug) {
                Bukkit.getLogger().log(Level.INFO, "[Metrics] " + ex.getMessage());
            }
            return 0;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void postPlugin(boolean isPing) throws IOException {
        PluginDescriptionFile description = this.plugin.getDescription();
        String pluginName = description.getName();
        boolean onlineMode = Bukkit.getServer().getOnlineMode();
        String pluginVersion = description.getVersion();
        String serverVersion = Bukkit.getVersion();
        int playersOnline = this.getOnlinePlayers();
        StringBuilder json = new StringBuilder(1024);
        json.append('{');
        Metrics.appendJSONPair(json, "guid", this.guid);
        Metrics.appendJSONPair(json, "plugin_version", pluginVersion);
        Metrics.appendJSONPair(json, "server_version", serverVersion);
        Metrics.appendJSONPair(json, "players_online", Integer.toString(playersOnline));
        String osname = System.getProperty("os.name");
        String osarch = System.getProperty("os.arch");
        String osversion = System.getProperty("os.version");
        String java_version = System.getProperty("java.version");
        int coreCount = Runtime.getRuntime().availableProcessors();
        if (osarch.equals("amd64")) {
            osarch = "x86_64";
        }
        Metrics.appendJSONPair(json, "osname", osname);
        Metrics.appendJSONPair(json, "osarch", osarch);
        Metrics.appendJSONPair(json, "osversion", osversion);
        Metrics.appendJSONPair(json, "cores", Integer.toString(coreCount));
        Metrics.appendJSONPair(json, "auth_mode", onlineMode ? "1" : "0");
        Metrics.appendJSONPair(json, "java_version", java_version);
        if (isPing) {
            Metrics.appendJSONPair(json, "ping", "1");
        }
        if (this.graphs.size() > 0) {
            Set<Graph> set = this.graphs;
            synchronized (set) {
                json.append(',');
                json.append('\"');
                json.append("graphs");
                json.append('\"');
                json.append(':');
                json.append('{');
                boolean firstGraph = true;
                for (Graph graph : this.graphs) {
                    StringBuilder graphJson = new StringBuilder();
                    graphJson.append('{');
                    for (Plotter plotter : graph.getPlotters()) {
                        Metrics.appendJSONPair(graphJson, plotter.getColumnName(), Integer.toString(plotter.getValue()));
                    }
                    graphJson.append('}');
                    if (!firstGraph) {
                        json.append(',');
                    }
                    json.append(Metrics.escapeJSON(graph.getName()));
                    json.append(':');
                    json.append(graphJson);
                    firstGraph = false;
                }
                json.append('}');
            }
        }
        json.append('}');
        URL url = new URL("http://report.mcstats.org" + String.format("/plugin/%s", Metrics.urlEncode(pluginName)));
        URLConnection connection = this.isMineshafterPresent() ? url.openConnection(Proxy.NO_PROXY) : url.openConnection();
        byte[] uncompressed = json.toString().getBytes();
        byte[] compressed = Metrics.gzip(json.toString());
        connection.addRequestProperty("User-Agent", "MCStats/7");
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("Content-Encoding", "gzip");
        connection.addRequestProperty("Content-Length", Integer.toString(compressed.length));
        connection.addRequestProperty("Accept", "application/json");
        connection.addRequestProperty("Connection", "close");
        connection.setDoOutput(true);
        if (this.debug) {
            System.out.println("[Metrics] Prepared request for " + pluginName + " uncompressed=" + uncompressed.length + " compressed=" + compressed.length);
        }
        OutputStream os = connection.getOutputStream();
        os.write(compressed);
        os.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String response = reader.readLine();
        os.close();
        reader.close();
        if (response == null || response.startsWith("ERR") || response.startsWith("7")) {
            if (response == null) {
                response = "null";
            } else if (response.startsWith("7")) {
                response = response.substring(response.startsWith("7,") ? 2 : 1);
            }
            throw new IOException(response);
        }
        if (response.equals("1") || response.contains("This is your first update this hour")) {
            Set<Graph> set = this.graphs;
            synchronized (set) {
                for (Graph graph : this.graphs) {
                    for (Plotter plotter : graph.getPlotters()) {
                        plotter.reset();
                    }
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] gzip(String input) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzos = null;
        try {
            gzos = new GZIPOutputStream(baos);
            gzos.write(input.getBytes("UTF-8"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (gzos != null) {
                try {
                    gzos.close();
                }
                catch (IOException e) {}
            }
        }
        return baos.toByteArray();
    }

    private boolean isMineshafterPresent() {
        try {
            Class.forName("mineshafter.MineServer");
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    private static void appendJSONPair(StringBuilder json, String key, String value) throws UnsupportedEncodingException {
        boolean isValueNumeric = false;
        try {
            if (value.equals("0") || !value.endsWith("0")) {
                Double.parseDouble(value);
                isValueNumeric = true;
            }
        }
        catch (NumberFormatException e) {
            isValueNumeric = false;
        }
        if (json.charAt(json.length() - 1) != '{') {
            json.append(',');
        }
        json.append(Metrics.escapeJSON(key));
        json.append(':');
        if (isValueNumeric) {
            json.append(value);
        } else {
            json.append(Metrics.escapeJSON(value));
        }
    }

    private static String escapeJSON(String text) {
        StringBuilder builder = new StringBuilder();
        builder.append('\"');
        block7 : for (int index = 0; index < text.length(); ++index) {
            char chr = text.charAt(index);
            switch (chr) {
                case '\"': 
                case '\\': {
                    builder.append('\\');
                    builder.append(chr);
                    continue block7;
                }
                case '\b': {
                    builder.append("\\b");
                    continue block7;
                }
                case '\t': {
                    builder.append("\\t");
                    continue block7;
                }
                case '\n': {
                    builder.append("\\n");
                    continue block7;
                }
                case '\r': {
                    builder.append("\\r");
                    continue block7;
                }
                default: {
                    if (chr < ' ') {
                        String t = "000" + Integer.toHexString(chr);
                        builder.append("\\u" + t.substring(t.length() - 4));
                        continue block7;
                    }
                    builder.append(chr);
                }
            }
        }
        builder.append('\"');
        return builder.toString();
    }

    private static String urlEncode(String text) throws UnsupportedEncodingException {
        return URLEncoder.encode(text, "UTF-8");
    }

    public static abstract class Plotter {
        private final String name;

        public Plotter() {
            this("Default");
        }

        public Plotter(String name) {
            this.name = name;
        }

        public abstract int getValue();

        public String getColumnName() {
            return this.name;
        }

        public void reset() {
        }

        public int hashCode() {
            return this.getColumnName().hashCode();
        }

        public boolean equals(Object object) {
            if (!(object instanceof Plotter)) {
                return false;
            }
            Plotter plotter = (Plotter)object;
            return plotter.name.equals(this.name) && plotter.getValue() == this.getValue();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Graph {
        private final String name;
        private final Set<Plotter> plotters = new LinkedHashSet<Plotter>();

        private Graph(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public void addPlotter(Plotter plotter) {
            this.plotters.add(plotter);
        }

        public void removePlotter(Plotter plotter) {
            this.plotters.remove(plotter);
        }

        public Set<Plotter> getPlotters() {
            return Collections.unmodifiableSet(this.plotters);
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public boolean equals(Object object) {
            if (!(object instanceof Graph)) {
                return false;
            }
            Graph graph = (Graph)object;
            return graph.name.equals(this.name);
        }

        protected void onOptOut() {
        }
    }

}

