package com.ecmdeveloper.plugin.util;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.filesystem.provider.FileStore;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;

public class ContentCache implements IPartListener2 {

	private Set<IPartService> partServicesListeningTo = new HashSet<IPartService>();
	private Set<String> cacheFiles = new HashSet<String>();
	
	public ContentCache() {
	}

	public IPath getTempFolderPath(IObjectStoreItem objectStoreItem) {
		
		IPath cacheLocation = getRootPath().append(getTempFolderName(objectStoreItem));

		if ( ! cacheLocation.toFile().exists() ) {
			cacheLocation.toFile().mkdir();
		}
		
		return cacheLocation;
	}

	public void clear() {
	    for ( File file : getRootPath().toFile().listFiles() ) {
	    	System.out.println( "Deleting " + file.toString() );
	    	deleteDirectory( file );
	    }
	}
	
	private boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					boolean result = files[i].delete();
					System.out.println( files[i].getName() + " " + result );
				}
			}
		}
		return (path.delete());
	}
	
	public IPath getRootPath() {
		
		IPath cacheLocation = Activator.getDefault().getStateLocation().append( "content_cache" );

		if ( ! cacheLocation.toFile().exists() ) {
			cacheLocation.toFile().mkdir();
		}
		
		return cacheLocation;
	}
	
	public void registerFile( String filename ) {
		cacheFiles.add(filename);
	}
	
	private String getTempFolderName(IObjectStoreItem objectStoreItem) {
		
		StringBuffer path = new StringBuffer();
		
		path.append( objectStoreItem.getObjectStore().getConnection().getName() );
		path.append( "_" );
		path.append( objectStoreItem.getObjectStore().getName() );
		path.append( "_" );
		path.append( objectStoreItem.getId() );
		
		return path.toString();
	}
	
	public void registerAsListener(IWorkbenchWindow window ) {
		
		partServicesListeningTo.add( window.getPartService() );
		window.getPartService().addPartListener(this);
	}

	public void stop() {
		
		for (IPartService partService : partServicesListeningTo ) {
			partService.removePartListener(this);
		}
		
		partServicesListeningTo.clear();
	}
	
	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		
		if ( partRef instanceof IEditorReference ) {
			try {
				IEditorInput editorInput = ((IEditorReference)partRef).getEditorInput();
//				FileStoreEditorInput f = (FileStoreEditorInput) editorInput;
//				System.out.println( f.getURI().toString() );
				System.out.println( editorInput.getClass().toString() );
//				if ( editorInput instanceof org.eclipse.ui.IIFileEditorInput )
				FileStore fileStore = (FileStore) editorInput.getAdapter( FileStore.class );

				if ( fileStore != null ) {
					System.out.println( "Got filestore");
				}
				File file = (File) editorInput.getAdapter( File.class );
				if ( file != null ) {
					System.out.println( "Got file" );
				}
				
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println( " The answer is " + partRef.getClass().toString() );
		}
		System.out.println( "----> Part closed" );
		// TODO Auto-generated method stub
		
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partHidden(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
	}

}
