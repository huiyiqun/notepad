import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.io.*;

public class Notepad extends Frame implements ActionListener, TextListener {
	private TextArea text;
	private boolean changed = false;
	private Clipboard clipboard = getToolkit().getSystemClipboard();
	private final static String proTitle = "记事本";
	private boolean goOn = false;
	private int flag = 0;
	private boolean linkToFile = false;
	private File linkFile = null;
	private Notepad point = this;
	private final String[] key_red = {"null", "true", "false"};
	private final String[] key_yellow = {"new", "break", "continue", "return", "do", "while", "if", "else", "for", "instanceof", "switch", "case", "default", "catch",
										 "finally", "throw", "try"};
	private final String[] key_purple = {"native", "import", "package"};
	private final String[] key_green = {"private", "protected", "public", "abstract", "class", "extends", "final", "implements", "interface", "static", "strictfp", 
										"synchronized", "transient", "volatile", "throws", "boolean", "byte", "char", "double", "float", "int", "long", "short",
										"super", "this", "void"};

	String path = null;
	String name = null;

	MenuBar mb;
	Menu file;
	Menu edit;
	Menu format;
	Menu help;
	MenuItem mi_open, mi_new, mi_save, mi_save2, mi_exit;
	MenuItem mi_copy, mi_cut, mi_paste, mi_find, mi_replace;
	MenuItem mi_font;
	MenuItem mi_about;

	public Notepad() {
		super(proTitle);
		setSize(1000, 600);
		setLayout(new BorderLayout());

		//About the window listener
		addWindowListener(new WindowManager());


		//About the menu bar
		mb = new MenuBar();
		setMenuBar(mb);


		file = new Menu("文件");
		mb.add(file);

		mi_open = new MenuItem("打开");
		mi_open.addActionListener(this);
		file.add(mi_open);

		mi_new = new MenuItem("新建");
		mi_new.addActionListener(this);
		file.add(mi_new);

		mi_save = new MenuItem("保存");
		mi_save.addActionListener(this);
		file.add(mi_save);

		mi_save2 = new MenuItem("另存为");
		mi_save2.addActionListener(this);
		file.add(mi_save2);

		mi_exit = new MenuItem("退出");
		mi_exit.addActionListener(this);
		file.add(mi_exit);
		

		edit = new Menu("编辑");
		mb.add(edit);

		mi_copy = new MenuItem("复制");
		mi_copy.addActionListener(this);
		edit.add(mi_copy);

		mi_cut = new MenuItem("剪切");
		mi_cut.addActionListener(this);
		edit.add(mi_cut);

		mi_paste = new MenuItem("粘贴");
		mi_paste.addActionListener(this);
		edit.add(mi_paste);

		mi_find = new MenuItem("查找");
		mi_find.addActionListener(this);
		edit.add(mi_find);

		mi_replace = new MenuItem("替换");
		mi_replace.addActionListener(this);
		edit.add(mi_replace);


		format = new Menu("格式");
		mb.add(format);

		mi_font = new MenuItem("字体");
		mi_font.addActionListener(this);
		format.add(mi_font);


		help = new Menu("帮助");
		mb.add(help);

		mi_about = new MenuItem("关于");
		mi_about.addActionListener(this);
		help.add(mi_about);

		//About the text area
		text = new TextArea();
		text.addTextListener(this);
		add(text, BorderLayout.CENTER);
	}

	public static void main(String[] args) {
		Notepad notepad1 = new Notepad();
		notepad1.setVisible(true);
	}

	public boolean open() {
		FileDialog loadDialog = new FileDialog(this, "打开", FileDialog.LOAD);
		if (name != null && path != null) {
			loadDialog.setFile(name);
			loadDialog.setDirectory(path);
		}
		loadDialog.setVisible(true);
		name = loadDialog.getFile();
		path = loadDialog.getDirectory();
		if (name == null || path == null)
			return false;
		File file = new File(path, name);
		if (file.exists()) {
			try {
				InputStream is = new FileInputStream(file);
				byte[] buf = new byte[is.available()];
				is.read(buf);
				String strBuf = new String(buf);
				flag++;
				text.setText(strBuf);
			}
			catch (IOException ioe) {
				System.err.println(ioe.toString());
			}
			changed = false;
			linkFile = file;
			linkToFile = true;
			this.setTitle(linkFile.getName());
			return true;
		}
		else
			return false;
	}

	public boolean saveTo() {
		FileDialog saveToDialog = new FileDialog(this, "另存为", FileDialog.SAVE);
		if (name != null && path != null) {
			saveToDialog.setFile(name);
			saveToDialog.setDirectory(path);
		}
		saveToDialog.setVisible(true);
		name = saveToDialog.getFile();
		path = saveToDialog.getDirectory();
		if (name != null && path != null) {
			File file = new File(path, name);
			try {
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(text.getText().getBytes());
				fos.close();
			}
			catch(Exception e) {
				System.err.println(e.toString());
			}
			linkToFile = true;
			linkFile = file;
			changed = false;
			this.setTitle(linkFile.getName());
			return true;
		}
		else
			return false;
	}

	public boolean save() {
		if (linkToFile) {
			try {
				FileOutputStream fos = new FileOutputStream(linkFile);
				fos.write(text.getText().getBytes());
				fos.close();
			}
			catch (Exception e) {
				System.err.println(e.toString());
			}
			changed = false;
			return true;
		}
		FileDialog saveDialog = new FileDialog(this, "保存", FileDialog.SAVE);
		if (name != null && path != null) {
			saveDialog.setFile(name);
			saveDialog.setDirectory(path);
		}
		saveDialog.setVisible(true);
		name = saveDialog.getFile();
		path = saveDialog.getDirectory();
		if (name != null && path != null) {
			File file = new File(path, name);
			try {
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(text.getText().getBytes());
				fos.close();
			}
			catch (Exception e) {
				System.err.println(e.toString());
			}
			changed = false;
			linkToFile = true;
			linkFile = file;
			this.setTitle(linkFile.getName());
			return true;
		}
		else
			return false;
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == mi_exit) {
			Thread thread = new Thread(new WindowManager());
			thread.start();
		}
		else if (ae.getSource() == mi_open) {
			Thread thread = new OpenThread();
			thread.start();
		}
		else if (ae.getSource() == mi_save) {
			save();
		}
		else if (ae.getSource() == mi_save2) {
			saveTo();
		}
		else if (ae.getSource() == mi_copy) {
			String temp = text.getSelectedText();
			StringSelection textS = new StringSelection(temp);
			clipboard.setContents(textS, null);
		}
		else if (ae.getSource() == mi_cut) {
			String temp = text.getSelectedText();
			StringSelection textS = new StringSelection(temp);
			clipboard.setContents(textS, null);
			text.replaceRange("", text.getSelectionStart(), text.getSelectionEnd());
		}
		else if (ae.getSource() == mi_paste) {
			Transferable contents = clipboard.getContents(this);
			DataFlavor flavor = DataFlavor.stringFlavor;
			if (contents.isDataFlavorSupported(flavor)) {
				try {
					String str;
					str = (String) contents.getTransferData(flavor);
					text.replaceRange(str, text.getSelectionStart(), text.getSelectionEnd());
				}
		   		catch(Exception ee) {
					System.err.println(ioe.toString());
				}
			}
		}
		else if (ae.getSource() == mi_new) {
			Thread thread = new NewThread();
			thread.start();
		}
		else if (ae.getSource() == mi_font) {
			Dialog fontDialog = new FontDialog(point);
			fontDialog.setVisible(true);
		}
		else if (ae.getSource() == mi_find) {
			FindDialog findDialog = new FindDialog(this);
			findDialog.setVisible(true);
		}
		else if (ae.getSource() == mi_replace) {
			ReplaceDialog replaceDialog = new ReplaceDialog(this);
			replaceDialog.setVisible(true);
		}
		else if (ae.getSource() == mi_about) {
			AboutDialog aboutDialog = new AboutDialog(this);
			aboutDialog.setVisible(true);
		}
	}

	class AboutDialog extends Dialog implements ActionListener {
		Button confirm;
		public AboutDialog(Frame owner) {
			super(owner, "关于", true);
			setSize(170, 150);
			confirm = new Button("确定");
			confirm.addActionListener(this);
			add(confirm, BorderLayout.SOUTH);
			addWindowListener(new DialogManager());
		}

		public void paint(Graphics g) {
			super.paint(g);
			g.drawString("记事本 by Myself", 10, 50);
			g.drawString("惠轶群制作", 10, 70);
			g.drawString("Tsinghua University", 10, 90);
			g.drawString("本软件以BSD权限分发", 10, 110);
		}

		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource() == confirm) {
				this.setVisible(false);
				this.dispose();
			}
		}
	}

	class FontDialog extends Dialog implements ActionListener {
		List fontNames;
		Button cancel;
		Button confirm;
		TextField size;
		List style;

		public FontDialog(Frame owner) {
			super(owner, "字体", true);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			String[] strNames = ge.getAvailableFontFamilyNames();
			fontNames = new List();
			int k;
			for (k = 0; k < strNames.length; k++)
				fontNames.add(strNames[k], k);
			for (k = 0; k < strNames.length; k++) {
				if (fontNames.getItem(k).equals(text.getFont().getFamily())) {
					fontNames.select(k);
					break;
				}
			}
			Panel center = new Panel();
			center.add(fontNames);
			size = new TextField(String.valueOf(text.getFont().getSize()), 5);
			center.add(size);
			style = new List();
			style.add("普通", Font.PLAIN);
			style.add("粗体", Font.BOLD);
			style.add("斜体", Font.ITALIC);
			style.add("粗斜体", Font.BOLD + Font.ITALIC);
			style.select(text.getFont().getStyle());
			center.add(style);
			Panel down = new Panel();
			confirm = new Button("确定");
			confirm.addActionListener(this);
			cancel = new Button("取消");
			cancel.addActionListener(this);
			down.add(confirm);
			down.add(cancel);
			add(center, BorderLayout.CENTER);
			add(down, BorderLayout.SOUTH);
			pack();
			addWindowListener(new DialogManager());
			setResizable(false);
		}

		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource() == cancel)
				;
			else if (ae.getSource() == confirm)
				text.setFont(new Font(fontNames.getSelectedItem(), style.getSelectedIndex(), Integer.parseInt(size.getText())));
			this.setVisible(false);
			this.dispose();
		}
	}

	class NewThread extends Thread {
		public void run() {
			if (!changed) {
				if (!text.getText().equals(""))
					flag++;
				text.setText("");
				linkToFile = false;
				setTitle(proTitle);
			}
			else {
				SaveWarning sw = new SaveWarning();
				sw.setVisible(true);
				while (sw.isVisible()) {
					yield();
				}
				if (goOn || !changed) {
					text.setText("");
					linkToFile = false;
					changed = false;
					setTitle(proTitle);
				}
			}
		}
	}

	class OpenThread extends Thread {
		public void run() {
			if (changed == false)
				open();
			else {
				SaveWarning sw = new SaveWarning();
				sw.setVisible(true);
				while (sw.isVisible())
					Thread.yield();
				if (goOn || !changed)
					open();
			}
		}
	}

	class DialogManager extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			we.getWindow().setVisible(false);
		}
	}

	public void textValueChanged(TextEvent e) {
		if (flag != 0)
			flag--;
		else
			changed = true;
	}

	class WindowManager extends WindowAdapter implements Runnable {
		public void windowClosing(WindowEvent we) {
			Thread thread = new Thread(this);
			thread.start();
		}

		public void run() {
			if(!changed)
				System.exit(0);
			else {
				java.awt.Toolkit.getDefaultToolkit().beep();
				SaveWarning sw = new SaveWarning();
				sw.setVisible(true);
				while (sw.isVisible())
					Thread.yield();
				if(!changed || goOn)
					System.exit(0);
			}
		}
	}

	class SaveWarning extends Dialog implements ActionListener {
		Button card;
		Button discard;
		Button cancel;
		public SaveWarning() {
			super(point, "警告", true);
			setResizable(false);
			setSize(300, 150);
			addWindowListener(new DialogManager());
			setLayout(new BorderLayout());
			Panel buttons = new Panel();

			card = new Button("保存");
			card.addActionListener(this);
			buttons.add(card);

			discard = new Button("不保存");
			discard.addActionListener(this);
			buttons.add(discard);

			cancel = new Button("取消");
			cancel.addActionListener(this);
			buttons.add(cancel);

			add(buttons, BorderLayout.SOUTH);
		}

		public void paint(Graphics g) {
			super.paint(g);
			g.drawString("是否保存您的更改？", 100, 70);
		}

		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource() == cancel) {
				goOn = false;
			}
			else if (ae.getSource() == discard) {
				goOn = true;
			}
			else if (ae.getSource() == card) {
				if (save())
					goOn = true;
				else
					goOn = false;
			}
			this.setVisible(false);
			this.dispose();
		}
	}

	class EndWarning extends Dialog implements ActionListener {
		Button confirm;
		public EndWarning(Dialog owner) {
			super(owner, "提示", true);
			setSize(200, 100);
			setResizable(false);
			confirm = new Button("确定");
			confirm.addActionListener(this);
			add(confirm, BorderLayout.SOUTH);
		}

		public void paint(Graphics g) {
			super.paint(g);
			g.drawString("已经到文件尾了", 65, 50);
		}

		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource() == confirm) {
				this.setVisible(false);
				this.dispose();
			}
		}
	}

	class FindDialog extends Dialog implements ActionListener {
		TextField find;
		Button next;
		Button cancel;
		public FindDialog(Frame owner) {
			super(owner, "查找");
			addWindowListener(new DialogManager());
			setResizable(false);
			find = new TextField("请在此输入您要查找的内容", 25);
			find.addActionListener(this);
			add(find, BorderLayout.CENTER);
			next = new Button("下一个");
			next.addActionListener(this);
			cancel = new Button("取消");
			cancel.addActionListener(this);
			Panel down = new Panel();
			down.add(next);
			down.add(cancel);
			add(down, BorderLayout.SOUTH);

			pack();
		}

		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource() == cancel) {
				setVisible(false);
			}
			else if (ae.getSource() == next || ae.getSource() == find) {
				String aim = find.getText();
				int start;
				if (text.getSelectionStart() == text.getSelectionEnd())
					start = text.getText().indexOf(aim, text.getSelectionStart());
				else
					start = text.getText().indexOf(aim, text.getSelectionStart() + 1);
				if (start == -1) {
					java.awt.Toolkit.getDefaultToolkit().beep();
					EndWarning ew = new EndWarning(this);
					ew.setVisible(true);
					text.setCaretPosition(0);
				}
				else
					text.select(start, start + aim.length());
			}
		}
	}

	class ReplaceDialog extends Dialog implements ActionListener {
		TextField find;
		TextField replace;
		Button next;
		Button replaceIt;
		Button replaceAll;
		Button cancel;
		public ReplaceDialog(Frame owner) {
			super(owner, "替换");
			addWindowListener(new DialogManager());
			setResizable(false);
			find = new TextField("请在此输入您被替换的内容", 25);
			find.addActionListener(this);
			replace = new TextField("请在此输入您准备替换为的内容", 25);
			replace.addActionListener(this);
			Panel center = new Panel();
			center.add(find);
			center.add(replace);
			add(center, BorderLayout.CENTER);
			next = new Button("下一个");
			next.addActionListener(this);
			replaceIt = new Button("替换");
			replaceIt.addActionListener(this);
			replaceAll = new Button("替换所有");
			replaceAll.addActionListener(this);
			cancel = new Button("取消");
			cancel.addActionListener(this);

			Panel down = new Panel();
			down.add(next);
			down.add(replaceIt);
			down.add(replaceAll);
			down.add(cancel);
			add(down, BorderLayout.SOUTH);

			pack();
		}

		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource() == cancel) {
				setVisible(false);
			}
			else if (ae.getSource() == next || ae.getSource() == find || ae.getSource() == replace) {
				String aim = find.getText();
				int start;
				if (text.getSelectionStart() == text.getSelectionEnd())
					start = text.getText().indexOf(aim, text.getSelectionStart());
				else
					start = text.getText().indexOf(aim, text.getSelectionStart() + 1);
				if (start == -1) {
					java.awt.Toolkit.getDefaultToolkit().beep();
					EndWarning ew = new EndWarning(this);
					ew.setVisible(true);
					text.setCaretPosition(0);
				}
				else
					text.select(start, start + aim.length());
			}
			else if (ae.getSource() == replaceIt)
				text.replaceRange(replace.getText(), text.getSelectionStart(), text.getSelectionEnd());
			else if (ae.getSource() == replaceAll)
				text.setText(text.getText().replace(find.getText(), replace.getText()));
		}
	}
}
