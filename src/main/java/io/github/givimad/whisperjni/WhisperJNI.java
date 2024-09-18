package io.github.givimad.whisperjni;

import io.github.givimad.whisperjni.internal.LibraryUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class WhisperJNI {
    private static boolean libraryLoaded;
    private static LibraryLogger libraryLogger;

    //region native api
    private native int init(String model, WhisperContextParams params);

    private native int initNoState(String model, WhisperContextParams params);

    private native int initState(int model);

    private native int loadGrammar(String text);

    private native void initOpenVINOEncoder(int model, String device);

    private native boolean isMultilingual(int model);

    private native int full(int context, WhisperFullParams params, float[] samples, int numSamples);

    private native int fullWithState(int context, int state, WhisperFullParams params, float[] samples, int numSamples);

    private native int fullNSegments(int context);

    private native int fullNSegmentsFromState(int state);

    private native long fullGetSegmentTimestamp0(int context, int index);

    private native long fullGetSegmentTimestamp1(int context, int index);

    private native String fullGetSegmentText(int context, int index);

    private native long fullGetSegmentTimestamp0FromState(int state, int index);

    private native long fullGetSegmentTimestamp1FromState(int state, int index);

    private native String fullGetSegmentTextFromState(int state, int index);

    private native void freeContext(int context);

    private native void freeState(int state);

    private native void freeGrammar(int grammar);

    private native String printSystemInfo();

    private native static void setLogger(boolean enabled);

//endregion

    
    public WhisperContext init(Path model) throws IOException {
        return init(model, null);
    }

    
    public WhisperContext init(Path model, WhisperContextParams params) throws IOException {
        assertModelExists(model);
        if(params == null) {
            params = new WhisperContextParams();
        }
        int ref = init(model.toAbsolutePath().toString(), params);
        if(ref == -1) {
            return null;
        }
        return new WhisperContext(this, ref);
    }

    
    public WhisperContext initNoState(Path model) throws IOException {
        return initNoState(model, null);
    }

    
    public WhisperContext initNoState(Path model, WhisperContextParams params) throws IOException {
        assertModelExists(model);
        if(params == null) {
            params = new WhisperContextParams();
        }
        int ref = initNoState(model.toAbsolutePath().toString(), params);
        if(ref == -1) {
            return null;
        }
        return new WhisperContext(this, ref);
    }

    
    public WhisperState initState(WhisperContext context) {
        WhisperJNIPointer.assertAvailable(context);
        int ref = initState(context.ref);
        if(ref == -1) {
            return null;
        }
        return new WhisperState(this, ref, context);
    }

    public WhisperGrammar parseGrammar(Path grammarPath) throws IOException {
        if(!Files.exists(grammarPath) || Files.isDirectory(grammarPath)){
            throw new FileNotFoundException("Grammar file not found");
        }
        return parseGrammar(new String(Files.readAllBytes(grammarPath)));
    }

    public WhisperGrammar parseGrammar(String text) throws IOException {
        if(text.isEmpty()) {
            throw new IOException("Grammar text is blank");
        }
        int ref = loadGrammar(text);
        if(ref == -1) {
            return null;
        }
        return new WhisperGrammar(this, ref, text);
    }

    
    public void initOpenVINO(WhisperContext context, String device) {
        WhisperJNIPointer.assertAvailable(context);
        initOpenVINOEncoder(context.ref, device);
    }

    
    public boolean isMultilingual(WhisperContext context) {
        WhisperJNIPointer.assertAvailable(context);
        return isMultilingual(context.ref);
    }

    
    public int full(WhisperContext context, WhisperFullParams params, float[] samples, int numSamples) {
        WhisperJNIPointer.assertAvailable(context);
        if(params.grammar != null) {
            WhisperJNIPointer.assertAvailable(params.grammar);
        }
        return full(context.ref, params, samples, numSamples);
    }

    
    public int fullWithState(WhisperContext context, WhisperState state, WhisperFullParams params, float[] samples, int numSamples) {
        WhisperJNIPointer.assertAvailable(context);
        WhisperJNIPointer.assertAvailable(state);
        if(params.grammar != null) {
            WhisperJNIPointer.assertAvailable(params.grammar);
        }
        return fullWithState(context.ref, state.ref, params, samples, numSamples);
    }

    
    public int fullNSegmentsFromState(WhisperState state) {
        WhisperJNIPointer.assertAvailable(state);
        return fullNSegmentsFromState(state.ref);
    }

    
    public int fullNSegments(WhisperContext context) {
        WhisperJNIPointer.assertAvailable(context);
        return fullNSegments(context.ref);
    }

    public long fullGetSegmentTimestamp0(WhisperContext context, int index) {
        WhisperJNIPointer.assertAvailable(context);
        return fullGetSegmentTimestamp0(context.ref, index);
    }

    public long fullGetSegmentTimestamp1(WhisperContext context, int index) {
        WhisperJNIPointer.assertAvailable(context);
        return fullGetSegmentTimestamp1(context.ref, index);
    }

    
    public String fullGetSegmentText(WhisperContext context, int index) {
        WhisperJNIPointer.assertAvailable(context);
        return fullGetSegmentText(context.ref, index);
    }

    
    public long fullGetSegmentTimestamp0FromState(WhisperState state, int index) {
        WhisperJNIPointer.assertAvailable(state);
        return fullGetSegmentTimestamp0FromState(state.ref, index);
    }

    
    public long fullGetSegmentTimestamp1FromState(WhisperState state, int index) {
        WhisperJNIPointer.assertAvailable(state);
        return fullGetSegmentTimestamp1FromState(state.ref, index);
    }

    
    public String fullGetSegmentTextFromState(WhisperState state, int index) {
        WhisperJNIPointer.assertAvailable(state);
        return fullGetSegmentTextFromState(state.ref, index);
    }

    
    public void free(WhisperContext context) {
        if (context.isReleased()) {
            return;
        }
        freeContext(context.ref);
        context.release();
    }

    
    public void free(WhisperState state) {
        if (state.isReleased()) {
            return;
        }
        freeState(state.ref);
        state.release();
    }

    
    public void free(WhisperGrammar grammar) {
        if (grammar.isReleased()) {
            return;
        }
        freeGrammar(grammar.ref);
        grammar.release();
    }

    
    public String getSystemInfo() {
        return printSystemInfo();
    }

    
    public static void loadLibrary() throws IOException {
        loadLibrary(null);
    }

    
    public static void loadLibrary(LoadOptions options) throws IOException {
        if (libraryLoaded) {
            return;
        }
        if (options == null) {
            options = new LoadOptions();
        }
        if(options.logger == null) {
            options.logger = (String ignored) -> { };
        }
        LibraryUtils.loadLibrary(options);
        libraryLoaded = true;
    }

    
    public static void setLibraryLogger(LibraryLogger logger) {
        libraryLogger = logger;
        setLogger(libraryLogger != null);
    }

    
    public interface LibraryLogger {
        void log(String text);
    }

    
    public static class LoadOptions {
        
        public LibraryLogger logger;
        
        public Path whisperJNILib;
        
        public Path whisperLib;
    }

    
    protected static void log(String text) {
        if (libraryLogger != null) {
            libraryLogger.log(text);
        }
    }

    
    protected static abstract class WhisperJNIPointer implements AutoCloseable {
        
        protected final int ref;
        private boolean released;

        
        protected static void assertAvailable(WhisperJNIPointer pointer) {
            if (pointer.isReleased()) {
                throw new RuntimeException("Unavailable pointer, object is closed");
            }
        }

        
        protected WhisperJNIPointer(int ref) {
            this.ref = ref;
        }

        
        protected boolean isReleased() {
            return released;
        }

        
        protected void release() {
            released = true;
        }
    }

    private static void assertModelExists(Path model) throws IOException {
        if (!Files.exists(model) || Files.isDirectory(model)) {
            throw new IOException("Missing model file: " + model);
        }
    }
}
