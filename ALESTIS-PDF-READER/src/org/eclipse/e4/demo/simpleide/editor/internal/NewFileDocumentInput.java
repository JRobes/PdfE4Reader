package org.eclipse.e4.demo.simpleide.editor.internal;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.demo.simpleide.editor.IDocumentInput;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;

public class NewFileDocumentInput extends AbstractInput implements IDocumentInput{
	private File file;
	private IDocument document;

	private static final int DEFAULT_FILE_SIZE = 15 * 1024;

	public NewFileDocumentInput(File file2) {
		this.file = file2;
	}

	public IStatus save() {
		System.err.println("Starting save");
		
		String encoding = "UTF-8";
		
		Charset charset= Charset.forName(encoding);
		CharsetEncoder encoder = charset.newEncoder();
		
		byte[] bytes;
		ByteBuffer byteBuffer;
		try {
			byteBuffer = encoder.encode(CharBuffer.wrap(document.get()));
			if (byteBuffer.hasArray())
				bytes= byteBuffer.array();
			else {
				bytes= new byte[byteBuffer.limit()];
				byteBuffer.get(bytes);
			}
			ByteArrayInputStream stream= new ByteArrayInputStream(bytes, 0, byteBuffer.limit());
			
			
			
			//ESTO HAY QUE CAMBIAR CUANDO SE QUIERE HACER UN SAVE
			//file.setContents(stream, true, true, null);
			setDirty(false);
			
		} catch (CharacterCodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.err.println("Saving done");
		
		return null;
	}

	public IDocument getDocument() {
		if (document == null) {
			//System.out.println("Dentro de NewFileDoc.... en getDocument cuando es null");
			Document document = new Document();
			document.set("");
			this.document= document;
		}

		return document;
	}
}
