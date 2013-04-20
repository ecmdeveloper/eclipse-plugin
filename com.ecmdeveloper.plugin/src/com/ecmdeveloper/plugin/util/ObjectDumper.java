package com.ecmdeveloper.plugin.util;

// Licensed Materials - Property of IBM
//
// 5748-R81
//
// (C) Copyright IBM Corp. 2009 All Rights Reserved
//
// US Government Users Restricted Rights - Use, duplication or
// disclosure restricted by GSA ADP Schedule Contract with
// IBM Corp.

// This code is provided "as is" and is not supported by any IBM organization
// or individual.  It's intended to illustrate the use of some programming
// paradigms.  Those illustrations may or may not be the "best" way of doing
// certain things.  The code could also contain bugs.

import java.io.*;
import java.util.*;

import com.filenet.api.action.PendingAction;
import com.filenet.api.collection.*;
import com.filenet.api.constants.PropertyState;
import com.filenet.api.core.*;
import com.filenet.api.property.Property;

/**
 * This is a utility class for dumping the contents of various kinds of P8 CE Java API
 * objects.  It's structured with many small protected methods so that you can use
 * subclassing for any changes you'd care to make instead of modifying this code
 * directly.
 * 
 * Methods in this class are generally not thread-safe.  You can reuse an instance of
 * this class if you first call the clear() method to reset internal state.
 * 
 * The dumping process is careful about avoiding changing any state of any of the objects
 * being dumped.
 */
public class ObjectDumper
{
    /**
     * Handy constant.  Used when dumping a null object.
     */
    protected static final byte[] NULL_AS_BYTES = "null".getBytes();
    /**
     * Handy constant.  Used when doing depth-related indentation.
     */
    protected static final byte[] SPACES_AS_BYTES = "  ".getBytes();
    /**
     * Handy constant.  Used when writing the end of a line after dumping an object.
     */
    protected static final byte[] NEWLINE_AS_BYTES = "\n".getBytes();

    private OutputStream os = null;
    private byte[] prefixTagAsBytes = null;
    /**
     * A convenience constructor, taking the defaults for the OutputStream 
     * and the prefix tag.
     */
    public ObjectDumper()
    {
        this(null, null);
    }
    /**
     * Specify the OutputStream to which you want to dump objects (defaults to System.out)
     * and a tag to use as a prefix for each line (defaults to empty string).
     */
    public ObjectDumper(OutputStream os, String prefixTag)
    {
        if (os == null) os = System.out;
        this.os = os;
        prefixTagAsBytes = (prefixTag != null ? prefixTag : "").getBytes();
    }
    /**
     * Resets internal state so that this instance can be reused.
     */
    public void clear()
    {
        previouslySeenObjects.clear();
    }
    /**
     * This convenience method is the equivalent of calling dump(o, 0).
     */
    public void dump(Object o) throws IOException
    {
        dump(o, 0);
    }
    /**
     * Dump an object.  It will be started with the given depth.  The depth will be
     * increased by 1 when dumping subordinate items.  The dumped information will
     * thereby show hierarchical relationships and nesting.
     * 
     * The convention for dump methods is that the parent object dumps the prefix
     * matter for its children.  The child object is only responsible for dumping itself
     * and its children in a fairly context-free way, including trailing newlines.
     */
    public void dump(Object o, int depth) throws IOException
    {
        depthHandler(depth);
        if (o == null)
        {
            dumpNull();
        }
        else if (isPreviouslySeen(o))
        {
            dumpPreviouslySeen(o);
        }
        else
        {
            os.write(formattedIdentity(o, false).getBytes());
            os.write(' ');
            os.write(simpleClassName(o).getBytes());
            os.write(' ');
            setPreviouslySeen(o);
            
            // The Big Switch Statement
            if (o instanceof EngineSet)
            {
                EngineSet engineSet = (EngineSet)o;
                dumpEngineSet(engineSet, depth);
            }
            else if (o instanceof Collection)
            {
                Collection collection = (Collection)o;
                dumpCollection(collection, depth);
            }
            else if (o instanceof Object[])
            {
                os.write(NEWLINE_AS_BYTES);
                Object[] oa = (Object[])o;
                dumpArray(oa, depth);
            }
            else if (o instanceof com.filenet.api.property.Properties)
            {
                com.filenet.api.property.Properties props = (com.filenet.api.property.Properties)o;
                dumpProperties(depth, props);
            }
            else if (o instanceof EngineObject)
            {
                EngineObject eo = (EngineObject)o;
                dumpEngineObject(depth, eo);
            }
            else if (o instanceof Property)
            {
                Property prop = (Property)o;
                dumpProperty(depth, prop);
            }
            else if (o instanceof Batch)
            {
                Batch batch = (Batch)o;
                dumpBatch(depth, batch);
            }
            else if (o instanceof BatchItemHandle)
            {
                BatchItemHandle bih = (BatchItemHandle)o;
                dumpBatchItemHandle(bih, depth);
            }
            else
            {
                os.write(o.toString().getBytes());
                os.write(NEWLINE_AS_BYTES);
            }
        }
    }
    /**
     * Dumping an EngineSet is slightly tricky because we don't know anything about
     * the state of any Iterator or PageIterator being used elsewhere.  In general, 
     * we can't even say if the collection is paged or if we have all of the items
     * already.  Consequently, we just dump information about the first page in the
     * collection.  The PageMark is opaque, so we just rely on the generic toString
     * output in case it reveals anything useful. 
     */
    protected void dumpEngineSet(EngineSet engineSet, int depth) throws IOException
    {
        PageIterator pi = engineSet.pageIterator();
        pi.nextPage();
        PageMark pm = pi.getPageMark();
        Object[] pageItems = pi.getCurrentPage();
        String summary = "n=" + pageItems.length + " " + pm;
        os.write(summary.getBytes());
        os.write(NEWLINE_AS_BYTES);
        dump(pageItems, depth);
    }
    /**
     * Collections of dependent objects implement java.util.List and so are
     * included here.
     */
    protected void dumpCollection(Collection collection, int depth) throws IOException
    {
        os.write(NEWLINE_AS_BYTES);
        for (Iterator it = collection.iterator(); it.hasNext();)
        {
            Object element = it.next();
            dump(element, depth+1);
        }
    }
    protected void dumpArray(Object[] oa, int depth) throws IOException
    {
        for (int ii = 0; ii < oa.length; ii++)
        {
            Object object = oa[ii];
            dump(object, depth+1);
        }
    }
    /**
     * Properties does not implement java.util.Collection, so it must be separately
     * handled.
     */
    protected void dumpProperties(int depth, com.filenet.api.property.Properties props) throws IOException
    {
        String summary = "n=" + props.size() + ", dirty=" + props.isDirty();
        os.write(summary.getBytes());
        os.write(NEWLINE_AS_BYTES);
        for (Iterator it = props.iterator(); it.hasNext();)
        {
            Property prop = (Property)it.next();
            dump(prop, depth+1);
        }
    }
    /**
     * There are a lot of different kinds of EngineObject, but the only distinction we
     * care about here is IndependentlyPersistableObject versus everything else.
     * For an IPO, we can get its ObjectReference instead of merely relying on the
     * generic toString output for identity information.
     */
    protected void dumpEngineObject(int depth, EngineObject eo) throws IOException
    {
        PendingAction[] actions = null;
        if (eo instanceof IndependentlyPersistableObject)
        {
            IndependentlyPersistableObject ipo = (IndependentlyPersistableObject)eo;
            actions = ipo.getPendingActions();
            os.write(ipo.getObjectReference().toString().getBytes());
        }
        else
        {
            os.write(eo.toString().getBytes());
        }
        os.write(NEWLINE_AS_BYTES);
        if (actions != null)
        {
            dump(actions, depth+1);
        }
        dump(eo.getProperties(), depth+1);
    }
    /**
     * A property can be dirty or not dirty.  We use the convention of marking dirty
     * properties with an asterisk.
     */
    protected void dumpProperty(int depth, Property prop) throws IOException
    {
        char isDirty = (prop.isDirty() ? '*' : ' ');
        PropertyState state = prop.getState();
        Object value = null;
        EngineCollection collection = null;
        if (state == PropertyState.NO_VALUE)
        {
            value = null;
        }
        else if (state == PropertyState.REFERENCE)
        {
            value = "((" + state.toString() + ")) " + prop;
        }
        else if (state == PropertyState.UNEVALUATED)
        {
            value = "((" + state.toString() + ")) " + prop;
        }
        else if (state == PropertyState.RETRIEVAL_ERROR)
        {
            value = "((" + state.toString() + ")) " + prop;
        }
        else
        {
            value = prop.getObjectValue();
        }

        if (value instanceof EngineCollection)
        {
            collection = (EngineCollection)value;
            value = "((collection))";
        }
        else if (value instanceof IndependentObject)
        {
            IndependentObject io = (IndependentObject)value;
            value = io.getObjectReference();
        }
        String summary = isDirty + prop.getPropertyName() + "=" + value;
        os.write(summary.getBytes());
        os.write(NEWLINE_AS_BYTES);
        if (collection != null)
        {
            dump(collection, depth+1);
        }
    }
    /**
     * Can be UpdatingBatch or RetrievingBatch.
     */
    protected void dumpBatch(int depth, Batch batch) throws IOException
    {
        os.write(NEWLINE_AS_BYTES);
        dump(batch.getBatchItemHandles(null), depth+1);
    }
    protected void dumpBatchItemHandle(BatchItemHandle bih, int depth) throws IOException
    {
        os.write(NEWLINE_AS_BYTES);
        if (bih.hasException())
        {
            dump(bih.getException(), depth+1);
        }
        else
        {
            dump(bih.getObject(), depth+1);
        }
    }
    /**
     * Method for dumping a null value.
     */
    protected void dumpNull() throws IOException
    {
        os.write(NULL_AS_BYTES);
        os.write(NEWLINE_AS_BYTES);
    }
    /**
     * Method for dumping a reference to a previously seen object.
     */
    protected void dumpPreviouslySeen(Object o) throws IOException
    {
        String line = formattedIdentity(o, true) + ' ' + simpleClassName(o);
        os.write(line.getBytes());
        os.write(NEWLINE_AS_BYTES);
    }
    /**
     * This utility method returns the runtime class of an object with any package name
     * components trimmed off.  There is also special handling for arrays.
     * If you are using Java 5 or later, you might like to replace this with an 
     * implementation that simply calls Class.getSimpleName(). 
     */
    protected String simpleClassName(Object o)
    {
        String name = o.getClass().getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot > 0  &&  !name.endsWith("."))
        {
            name = name.substring(lastDot + 1);
        }
        if (o instanceof Object[])
        {
            Object[] oa = (Object[])o;
            if (name.endsWith(";"))
            {
                name = name.substring(0, name.length()-1);
            }
            name += "[" + oa.length + "]";
        }
        return name;
    }
    /**
     * This utility routine formats the object's identity for display.  The implementation
     * uses the identity hash code, making it a uniform length of 8 characters.  For our
     * own convenience, it also makes a distinction between previously seen objects and
     * newly seen objects.
     */
    protected String formattedIdentity(Object o, boolean previouslySeen)
    {
        int identity = System.identityHashCode(o);
        char surroundLeft  = (previouslySeen ? '^' : '[');
        char surroundRight = (previouslySeen ? '^' : ']');
        String raw = "0000000" + Integer.toHexString(identity);
        return surroundLeft + raw.substring(raw.length() - 8) + surroundRight;
    }
    /**
     * The default implementation of this method indents two spaces for every level
     * of depth.  If you want something more elaborate, create a subclass and override
     * this method.
     * @throws IOException 
     */
    protected void depthHandler(int depth) throws IOException
    {
        os.write(prefixTagAsBytes);
        for(int ii=0; ii<depth; ++ii)
        {
            os.write(SPACES_AS_BYTES);
        }
    }
    
    /**
     * This is a simple Comparator that only worries about exact object instance identity.
     * That's all we care about for keeping track of previously seen objects.
     */
    private static class SameInstance implements Comparator
    {
        public int compare(Object o1, Object o2)
        {
            return System.identityHashCode(o1) - System.identityHashCode(o2);
        }
    }
    /**
     * When objects are dumped the first time, they are added to this list of previously
     * seen objects.  Later dumps will check this list and just dump a reference to the
     * first appearance.  That makes the dumps more compact and also avoids any problems
     * with infinite recursion due to loops in reference chains.
     */
    private Set previouslySeenObjects = new java.util.TreeSet(new SameInstance());
    /**
     * Self-explanatory.
     */
    private boolean isPreviouslySeen(Object o)
    {
        return previouslySeenObjects.contains(o);
    }
    /**
     * Self-explanatory.
     */
    private void setPreviouslySeen(Object o)
    {
        previouslySeenObjects.add(o);
    }
}
