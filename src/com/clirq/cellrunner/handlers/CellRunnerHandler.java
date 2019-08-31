package com.clirq.cellrunner.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleInputStream;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

public class CellRunnerHandler extends AbstractHandler {

	public void otherWay(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
		IJavaModel javaModel = JavaCore.create(workspaceRoot);

		{
			ISelection sel = HandlerUtil.getCurrentSelection(event);

			if (sel instanceof IStructuredSelection) {
				// check to see if it's empty first, though
				IStructuredSelection isel = (IStructuredSelection) sel;
				Object obj = isel.getFirstElement();

				// then have a look and see what your selection is.

			}

		}

	}
	
	String prefix="JavaNotebook";
	public IOConsole getConsole(ExecutionEvent event) throws ExecutionException {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		List<String> names=new ArrayList<>(existing.length);
		for (int i = 0; i < existing.length; i++) {
			IConsole iconsole = existing[i];
			if (iconsole.getName().startsWith(prefix) && iconsole instanceof IOConsole) {
				IOConsole console=(IOConsole) iconsole;
				return console;

			}
		}
		return null;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		ITextEditor editor = (ITextEditor) page.getActiveEditor();
		IJavaElement elem = JavaUI.getEditorInputJavaElement(editor.getEditorInput());
		
		if (elem instanceof ICompilationUnit) {
			ITextSelection sel = (ITextSelection) editor.getSelectionProvider().getSelection();
			IOConsole console = getConsole(event);

			if (console!=null) {
				String toConsole;
				if (sel.getLength() > 0) {
					toConsole = sel.getText().replace(System.lineSeparator(), "\t");
				} else {
					IJavaElement selected;
					try {
						selected = ((ICompilationUnit) elem).getElementAt(sel.getOffset());
						if (selected != null && selected.getElementType() == IJavaElement.METHOD) {
							IMethod method = (IMethod) selected;
							toConsole = String.format("%s\t%s", method.getElementName(),
									method.getAncestor(IJavaElement.TYPE).getElementName());
							/*
							MessageDialog.openInformation(window.getShell(), "Notebook",
									String.format("Selection length: %d, selection `%s`, Enclosing method: %s in class %s",sel.getLength(), sel.getText(),
											method.getElementName(),
											method.getAncestor(IJavaElement.TYPE).getElementName()));
							*/
						} else {
							MessageDialog.openInformation(window.getShell(), "Notebook",
									"Cursor is outside of a method body");
							return null;
						}
					} catch (JavaModelException e) {
						MessageDialog.openInformation(window.getShell(), "Notebook",
								"Could not find the name of the enclosing method");
						e.printStackTrace();
						return null;
					}
				}
				IOConsoleInputStream cis = console.getInputStream();
				cis.appendData(toConsole + System.lineSeparator());
				return null;
			}else {
				MessageDialog.openInformation(window.getShell(), "Notebook",
						"Could not find a running Notebook instance");
				return null;
			}
		}else {
			MessageDialog.openInformation(window.getShell(), "Notebook",
					"Cursor is outside of a compilation unit");
			return null;
		}
	}
}
