import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

/*
 *	� ���� ������������ ������ ������������ �����, ��� �������� ������������� ��������.
 *	����, � �������, ���������� ����������� ������������ ��� ��������� ������� �������,
 *	�� ����� ������������ StringBuffer ��� �������� �����.
 */

// ��������
class Property
{
	/* Type
	 *	0 // 0 - ���������, 1 - �������, 2 - �������
	 *	1 // 0 - �� �����������, 1 - ���������� ������������ ������, 2 - ����������� ������������ ���� ���������������� ������
	 *	2 // 0 - ���������, 1 - �������
	 *	3 // ������ ���������� ������
	 *	4 // ����� ��� ������� ��� ���������� ������
	 *	5 // ����� �������� ��� ���������� ������
	 *	6 // ������ �������� (������ ������ ���������� ���������)
	 */

	// ��� ��������
	private int Type;
	// �������� ��������
	private int Value = -1;

	// ��������� ����������� �������� (������)
	final static public int EMPTY_PROPERTY = 6;

	// ������������� c �������� ��������� ���� ��������
	Property(int Type)
	{
		this.Type = Type;
	}

	// ������������� c �������� ��������� ���� �������� � �� ��������� ��������
	Property(int Type, int Value)
	{
		this.Type = Type;
		this.Value = Value;
	}

	// ������������� ��������-��������
	Property()
	{
		Type = EMPTY_PROPERTY;
	}

	public void setValue(int Value)
	{
		this.Value = Value;
	}

	// �������� ��� ��������
	public int type()
	{
		return Type;
	}

	public int value()
	{
		return Value;
	}
}

// ����������� �����, �������� ������ � �����������
class VFigure
{
	// ��� �� ������
	protected int Class;

	// ������ �������
	protected Property[] Properties = new Property[10];
	protected int PropCount;

	// �������� ����� �������� ������
	public void addProperty(Property Prope)
	{
		// ���� �������� � �������� �������� ������
		if (Prope.type() != Property.EMPTY_PROPERTY && Prope.value() != -1)
		{
			boolean duplicateOrAbort = false;

			// ����, �� ���� �� ��� ������ ��������
			for (int i = 0; i < PropCount; i++)
			{
				// ���� ����� �������� ����
				if (Properties[i].type() == Prope.type())
				{
					if (
						Properties[i].type() == 1
						&&
						Properties[i].value() == 1
						&&
						Prope.value() > 0
						)
					{	// ���� ������ ���� ����������� ������������ ������
						// ��������� �������������� ������������ ���� ����������������� ������
						Properties[i].setValue(2);
					}
					else if (
							Properties[i].type() == 4
							&&
							Properties[i].value() < Prope.value()
							)
					{	// ���� ����������� ��������� ������ ����������� ������������
						Properties[i].setValue(Prope.value());
					}
					else if (
							Properties[i].type() == 5
							&&
							Properties[i].value() > Prope.value()
							)
					{	// ���� ������������ ��������� ������ ����������� �������������
						Properties[i].setValue(Prope.value());
					}

					// � ����� ������, ��� ������� �� ����� ���������
					duplicateOrAbort = true;
				}
			}

			/* ��������� �� ����������� ������������� ������ ��������,
			 * ��������������� ���������� ������ ������ */
			if (!duplicateOrAbort && (Prope.type() == 4 || Prope.type() == 5 || Prope.type() == 3))
			{
				for (int i = 0; i < PropCount; i++)
				{
					if (Properties[i].type() == 3)
					{	// ���� ��� ���� ��������, ��������������� ������ ���������� ������,
						// �� ����� �������� �������� ��������� ������
						duplicateOrAbort = true;
						break;
					}
				}

				if (!duplicateOrAbort)
				{
					if (Prope.type() == 4)
					{ // ���� ����� �������� ������������� ����������� ���������� ������
						for (int i = 0; i < PropCount; i++)
						{
							// ���� ���� �������� ��������
							if (Properties[i].type() == 5)
							{
								// ���� �����������
								if (Properties[i].value() == Prope.value())
								{	// ���� ��� ����������� ������� ���� �����
									// �������� ���������� �������� �� �����
									Properties[i] = new Property(3, Prope.value());
									// ������� � ���, ��� �� ���� ������� ����� ��������
									duplicateOrAbort = true;
									break;
								}
								else if (Properties[i].value() < Prope.value())
								{	// ���� ��� ����������� �� ������� ��������
									// ������� � ���, ��� �� ���� ��������������� ������ ��������
									duplicateOrAbort = true;
									break;
								}
								// ����� �������� ����� ��������
							}
						}
					}
					else if (Prope.type() == 5)
					{ // ���� ����� �������� ������������� ������������ ���������� ������
						for (int i = 0; i < PropCount; i++)
						{
							// ���� ���� �������� ��������
							if (Properties[i].type() == 4)
							{
								// ���� �����������
								if (Properties[i].value() == Prope.value())
								{	// ���� ��� ����������� ������� ���� �����
									// �������� ���������� �������� �� �����
									Properties[i] = new Property(3, Prope.value());
									// ������� � ���, ��� �� ���� ������� ����� ��������
									duplicateOrAbort = true;
									break;
								}
								else if (Properties[i].value() > Prope.value())
								{	// ���� ��� ����������� �� ������� ��������
									// ������� � ���, ��� �� ���� ��������������� ������ ��������
									duplicateOrAbort = true;
									break;
								}
								// ����� �������� ����� ��������
							}
						}
					}
					else // ���� ����� �������� ������������� ������ ���������� ������
					{
						int min = -1, max = 10000;

						// ���� ����������� � ������������ ���������� ������, ���� ������� ������
						for (int i = 0; i < PropCount; i++)
						{
							if (Properties[i].type() == 4)
								min = Properties[i].value();
							else if (Properties[i].type() == 5)
								max = Properties[i].value();
						}

						// ���� ����� �������� ������ � ���������� ��������
						if (Prope.value() >= min && Prope.value() <= max)
						{
							// ���� ��� ����� ����������� �����, ������� � �������� ���
							if (min != -1)
								for (int i = 0; i < PropCount; i++)
								{
									if (Properties[i].type() == 4)
										Properties[i] = new Property();
								}

							// ���� ��� ����� ������������ �����, ������� � �������� ���
							if (min != 10000)
								for (int i = 0; i < PropCount; i++)
								{
									if (Properties[i].type() == 4)
										Properties[i] = new Property();
								}
							// ���������� �������� ������
						}
						else // ���� �������� ������� �� ���������
						{	// �� ���������� ����� ��������
							duplicateOrAbort = true;
						}
					}
				}
			}

			// ���� �� � �������
			if (!duplicateOrAbort)
			{
				// ��������� �������� ������
				Properties[PropCount] = Prope;
				PropCount++;
			}
		}
	}

	public Property getProperty(int PropID)
	{
		return Properties[PropID];
	}

	public int propertyCount()
	{
		return PropCount;
	}

	public int fclass()
	{
		return Class;
	}
}

// �������� ������ (����������� ������)
class ProtoFigure extends VFigure
{
	// ��������
	private int Parent;

	// �������������
	ProtoFigure(int Class, int Parent)
	{
		this.Class = Class;
		this.Parent = Parent;
		PropCount = 0;
	}

	public int parent()
	{
		return Parent;
	}

	// ��������� ��� �������� ������� ���������
	void addPropertiesFromAnotherPF(ProtoFigure Proto)
	{
		for (int i = 0; i < Proto.propertyCount(); i++)
		{
			addProperty(Proto.getProperty(i));
		}
	}
}

// �������� ������
class Figure extends VFigure
{
	// �������������
	Figure(int Class)
	{
		this.Class = Class;
		PropCount = 0;
	}

	// ������������� ������ �������
	Figure(Figure fig)
	{
		Class = fig.Class;
		PropCount = 0;
		for (int i = 0; i < fig.propertyCount(); i++)
			addProperty(new Property(fig.getProperty(i).type(), fig.getProperty(i).value()));
	}

	// ������������� �� ������ ���������
	Figure(ProtoFigure Prototype)
	{
		this.Class = Prototype.fclass();
		PropCount = 0;
		for (int i = 0; i < Prototype.propertyCount(); i++)
			addProperty(new Property(Prototype.getProperty(i).type(), Prototype.getProperty(i).value()));
	}

	public int size()
	{
		for (int i = 0; i < PropCount; i++)
		{
			if (Properties[i].type() == 0)
				return Properties[i].value();
		}

		// ���� �� ���������� ������, ������ "�������"
		return 1;
	}

	// ������ ����� ������ �� ����� ����-������������������, ���� ��� ��������
	public boolean refreshClass()
	{
		// ��� ������� ��������� ������
		for (int i = 0; i < TextToGraphic.FCount; i++)
		{
			// ���� ������ �������� ����������� ������ ������
			if (TextToGraphic.ProtoFigures[i].parent() == Class)
			{
				boolean hasErrors = false;

				// ���� ����������� � ������������ ���������� ������ �����������
				int MinVertsPF = -1, MaxVertsPF = 10000;
				for (int j = 0; j < TextToGraphic.ProtoFigures[i].propertyCount(); j++)
				{
					if (TextToGraphic.ProtoFigures[i].Properties[j].type() == 3)
					{	// ���� ��������������� ������ ���������� ������
						// ������������� ������������ � ����������� ���������� ������
						MinVertsPF = TextToGraphic.ProtoFigures[i].Properties[j].value();
						MaxVertsPF = MinVertsPF;
						// ������ ��� ������ ���-�� ������
						break;
					}
					else if (TextToGraphic.ProtoFigures[i].Properties[j].type() == 4)
					{
						// ������������� ����������� ���������� ������
						MinVertsPF = TextToGraphic.ProtoFigures[i].Properties[j].value();
					}
					else if (TextToGraphic.ProtoFigures[i].Properties[j].type() == 5)
					{
						// ������������� ������������ ���������� ������
						MaxVertsPF = TextToGraphic.ProtoFigures[i].Properties[j].value();
					}
				}

				// ���� ����������� � ������������ ���������� ������ ����� ������
				int MinVertsOF = -1, MaxVertsOF = 10000;
				for (int j = 0; j < PropCount; j++)
				{
					if (Properties[j].type() == 3)
					{	// ���� ��������������� ������ ���������� ������
						MinVertsOF = Properties[j].value();
						MaxVertsOF = MinVertsOF;
						// ������ ��� ������ ���-�� ������
						break;
					}
					else if (Properties[j].type() == 4)
					{
						// ������������� ������������ ���������� ������
						MinVertsOF = Properties[j].value();
					}
					else if (Properties[j].type() == 5)
					{
						// ������������� ����������� ���������� ������
						MaxVertsOF = Properties[j].value();
					}
				}

				// ���� ���������� ����� �� ��������
				if (MinVertsOF != MinVertsPF || MaxVertsOF != MaxVertsPF)
				{
					hasErrors = true;
				}

				// ���� ��� ��������������
				if (!hasErrors)
				{
					// ������������ ��� ��������
					for (int j = 0; j < TextToGraphic.ProtoFigures[i].propertyCount(); j++)
					{
						// ���� �������� �� ������ � �� ��������� � ���������� ������
						if (TextToGraphic.ProtoFigures[i].Properties[j].type() != Property.EMPTY_PROPERTY && (TextToGraphic.ProtoFigures[i].Properties[j].type() < 3 || TextToGraphic.ProtoFigures[i].Properties[j].type() > 5))
						{
							boolean propertyAccepted = false;

							// ���� ��� ����� �������� ����������� ��������������� �������� ������
							int localType = TextToGraphic.ProtoFigures[i].Properties[j].type();
							for (int k = 0; k < PropCount; k++)
							{
								// ���� ����� ����������� ��������
								if (Properties[k].type() == localType)
								{
									// ���� �������� ��������� � ��������������
									if (localType == 1)
									{
										if (TextToGraphic.ProtoFigures[i].Properties[j].value() == Properties[k].value() || (TextToGraphic.ProtoFigures[i].Properties[j].value() == 1 && Properties[k].value() == 2))
										{	// ���� �������������� �� �������������
											// ������� � ���, ��� ����� �������� � ������� �� �����
											propertyAccepted = true;
											break;
										}
									}
									else // ���� �������� �� ��������� � ��������������
									{
										if (TextToGraphic.ProtoFigures[i].Properties[j].value() == Properties[k].value())
										{	// ���� �������� ������� ���������
											// ������� � ���, ��� ����� �������� � ������� �� �����
											propertyAccepted = true;
											break;
										}
										else // ���� �������� ��������
										{
											// ������ �� ��������
											hasErrors = true;
											break;
										}
									}
								}
							}

							if (!propertyAccepted)
							{	// ���� �������� �� ��� ������ ������
								// ������ ��� �� ��������
								hasErrors = true;
								break;
							}

							if (hasErrors)
							{	// ���� �������� ��� ������ �������������� ������
								// ��� ������ ��� �� ��������
								break;
							}
						}
					}
				}

				// ���� ������ ��� ��������
				if (!hasErrors)
				{
					// ���������� � �����
					Class = TextToGraphic.ProtoFigures[i].fclass();

					// ������� ����� �������� ��� ����� ������
					refreshClass();

					return true;
				}
			}
		}
		return false;
	}

	// ������������ ������ ������
	public void draw(Graphics g, int xPos, int yPos, int Wid)
	{
		// ������ ��������� ������
		switch (Class)
		{
			case 0: // ������
				break;
			case 1: // ����
				g.drawOval(xPos - Wid/2, yPos - Wid/2 + Wid / 4, Wid, Wid / 2);
				break;
			case 2: // ������
				g.drawOval(xPos - Wid/2, yPos - Wid/2 + Wid / 4, Wid, Wid / 2);
				break;
			case 3: // ����
				g.drawOval(xPos - Wid/2, yPos - Wid/2, Wid, Wid);
				break;
			case 4: // �������
				break;
			case 5: // 4-��������
				break;
			case 6: // �������������
				g.drawRect(xPos - Wid/2, yPos - Wid/2 + Wid / 4, Wid, Wid / 2);
				break;
			case 7: // �������
				g.drawRect(xPos - Wid/2, yPos - Wid/2, Wid, Wid);
				break;
			case 8: // �����������
				int[] arrX = {xPos - Wid / 2, xPos + Wid / 2, xPos};
				int[] arrY = {yPos + Wid / 2, yPos + Wid / 2, yPos - Wid / 2};
				g.drawPolygon(arrX, arrY, 3);
				break;
			case 9: // �������������
				break;
			default:
				break;
		}
	}
}

// ��������� �� �����
class FiguresCollection
{
		// ��� ������
	public Figure FirstFigure, SecondFigure;

	/* ��� �����
	 *	0 - ��������� ������
	 *	1 - ������ 1 � ������ 2
	 *	2 - ������ ������������
	 *	3 - ������ 1 ��� ������� 2
	 *	4 - ������ 1 ��� ������� 2
	 *	5 - ������ 1 ����� �� ������ 2
	 *	6 - ������ 1 ������ �� ������ 2
	 */
	int ConnectionType;

	// ����������
	FiguresCollection(Figure fig1, Figure fig2, int type)
	{
		FirstFigure = fig1;
		SecondFigure = fig2;
		ConnectionType = type;
	}

	// ���������� ������ ���������� (�� ��������)
	FiguresCollection(FiguresCollection FC)
	{
		FirstFigure = new Figure(FC.FirstFigure);
		SecondFigure = new Figure(FC.SecondFigure);
		ConnectionType = FC.ConnectionType;
	}

	// ���������� ��������� �������
	FiguresCollection(Figure fig)
	{
		FirstFigure = new Figure(fig);
		ConnectionType = 0;
	}

	// ��������� ������ ������
	public void addSecondFigure(Figure fig)
	{
		SecondFigure = new Figure(fig);
	}

	public void setType(int type)
	{
		ConnectionType = type;
	}

	public void draw(Graphics g, int xPos, int yPos, int Wid)
	{
		// ������������ 0 - ���������, 1 - ����������, 2 �������
		int FstWid = (int)Math.round(Wid * (FirstFigure.size() + 1) / 3.0);
		int SndWid = 0;
		int conType = 0;
		if (SecondFigure != null)
		{
			SndWid = (int)Math.round(Wid * (SecondFigure.size() + 1) / 3.0);
			conType = ConnectionType;
		}

		switch (conType)
		{
			case 0:
				FirstFigure.draw(g, xPos, yPos, FstWid);
				break;
			case 1:
				FirstFigure.draw(g, xPos, yPos, FstWid/3);
				SecondFigure.draw(g, xPos, yPos, SndWid);
				break;
			case 2:
				FirstFigure.draw(g, xPos-FstWid / 10, yPos-FstWid / 12, FstWid);
				SecondFigure.draw(g, xPos+FstWid / 10, yPos+FstWid / 12, SndWid);
				break;
			case 3:
				FirstFigure.draw(g, xPos, yPos - FstWid/4, FstWid/3 + FstWid/7);
				SecondFigure.draw(g, xPos, yPos + FstWid/4, SndWid/3 + SndWid/7);
				break;
			case 4:
				FirstFigure.draw(g, xPos, yPos + FstWid/4, FstWid/3 + FstWid/7);
				SecondFigure.draw(g, xPos, yPos - FstWid/4, SndWid/3 + SndWid/7);
				break;
			case 5:
				FirstFigure.draw(g, xPos - FstWid/4, yPos, FstWid/3 + FstWid/7);
				SecondFigure.draw(g, xPos + FstWid/4, yPos, SndWid/3 + SndWid/7);
				break;
			case 6:
				FirstFigure.draw(g, xPos + FstWid/4, yPos, FstWid/3 + FstWid/7);
				SecondFigure.draw(g, xPos - FstWid/4, yPos, SndWid/3 + SndWid/7);
				break;
			default:
				FirstFigure.draw(g, xPos, yPos, FstWid);
				break;
		}
	}
}

// ������ ��������� �����
class FiguresMass
{
	private FiguresCollection[] Mass;
	private int Count;

	// ���������� ������
	FiguresMass()
	{
		Mass = new FiguresCollection[50];
		Count = 0;
	}

	// ������� ������
	public void reset()
	{
		Count = 0;
	}

	// ����� ���������� ���������
	public int count()
	{
		return Count;
	}

	// ���� ��������� �����
	public FiguresCollection getFigure(int pos)
	{
		return Mass[pos];
	}

	// ��������� ��������� �����
	public void insertCollection(FiguresCollection FC)
	{
		Mass[Count] = new FiguresCollection(FC);
		Count++;
	}

	// ��������� ��������� ������
	public void insertFigure(Figure fig)
	{
		Mass[Count] = new FiguresCollection(fig);
		Count++;
	}

	// ������������ ������ ��������
	public void draw(Graphics g, int ImageX, int ImageY, int ImageSize)
	{
		int xPos = 0, yPos = 0, xSize = 1, ySize = 0, Wid = 0;

		boolean isFinish = false;
		while (!isFinish)
		{
			if (Count <= xSize * xSize)
			{
				ySize = Count / xSize + 1;
				Wid = ImageSize/xSize;
				isFinish = true;
			}
			else
			{
				xSize++;
			}
		}

		for (int i = 0; i < Count; i++)
		{
			xPos = i%xSize;
			yPos = i/xSize;
			Mass[i].draw(g, xPos * Wid + Wid / 2 + ImageX, yPos * Wid + Wid / 2 + ImageY, (int)Math.round(Wid * 0.9));
		}
	}
}

// ���� ����
class Window1 extends Frame
{
	// ������ �����
	FiguresMass Figures = new FiguresMass();

	// ������� ����
	TextArea Field;
	Label Label1;
	TextArea Area;
	MenuBar MenuB = new MenuBar();
	Checkbox CB;

	// �������������
	Window1(String s)
	{
		super(s);
		// ��������� ����
		setBounds(300, 250, 100, 30);
		setLayout(null);

		// �������� ����������� �������� ����
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent ev)
			{
				// ������� � �������
				TextToGraphic.Log("�������� ����������");
				System.exit(0);
			}
		});

		// ������ �����
		Label1 = new Label();
		Label1.setBounds(500, 410, 900, 30);
		add(Label1);

		// ���� ������
		Area = new TextArea("", 50, 50, TextArea.SCROLLBARS_BOTH);
		Area.setEditable(false);
		Area.setBounds(500, 100, 450, 250);
		add(Area);

		// �������� ���� �����
		Field = new TextArea("", 50, 50, TextArea.SCROLLBARS_VERTICAL_ONLY);
		//Field.setEditable(false);
		Field.setBounds(25, 360, 450, 70);
		add(Field);

		// �������� ������ 1
		Button Btn1 = new Button("����������");
		// ���������� ������
		Btn1.setBounds(500, 380, 100, 30);
		add(Btn1);

		// �������� ������ 2
		Button Btn2 = new Button("��������");
		// ���������� ������
		Btn2.setBounds(630, 380, 100, 30);
		add(Btn2);

		CB = new Checkbox("������������� � �������� �������", true);
		CB.setBounds(500, 350, 300, 30);
		add(CB);

		// ���������� ����� ������
		Field.addTextListener(new ActLis(this));

		// ������� ������ "����������"
		Btn1.addActionListener(new ActLis(this));

		// ������� ������ "��������"
		Btn2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{	// ������� ���� �����
				Field.replaceRange("", 0, 10000);
			}
		});

		// ������� ���� ����������
		setMenuBar(MenuB);

		Menu MFile = new Menu("����");
		Menu MHelp = new Menu("�������");

		MenuB.add(MFile);
		MenuB.add(MHelp);

		MenuItem MIOpen = new MenuItem("������� ����", new MenuShortcut(KeyEvent.VK_O));
		MenuItem MISave = new MenuItem("��������� �����..", new MenuShortcut(KeyEvent.VK_S));
		MenuItem MIExit = new MenuItem("�����", new MenuShortcut(KeyEvent.VK_Q));

		MenuItem MIAbout = new MenuItem("� ���������");

		MFile.add(MIOpen);
		MFile.add(MISave);
		MFile.addSeparator();
		MFile.add(MIExit);

		MHelp.add(MIAbout);

		// ������� ����
		MIOpen.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				FileDialog fd = new FileDialog(new Frame(),	"������� ����", FileDialog.LOAD);
				fd.setVisible(true);
				if (fd.getFile() != null)
				{
					String FilePath = fd.getDirectory()+fd.getFile();
					readFile(FilePath);
				}
			}
		});

		// ��������� �����
		MISave.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				FileDialog fd = new FileDialog(new Frame(),	" ��������� ���..", FileDialog.LOAD);
				fd.setVisible(true);
				if (fd.getFile() != null)
				{
					String FilePath = fd.getDirectory()+fd.getFile();
					writeToFile(FilePath);
				}
			}
		});

		// �����
		MIExit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});

		// � ���������
		MIAbout.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(null, "������������� ������\n������� ��� ��-09 ����� �������\n2013�", "� ���������", JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}

	// ��������� ����������� ����
	public void paint(Graphics g)
	{
		Figures.draw(g, 100, 50, 300);
	}

	// ������� ���������� ����� � Field
	public void readFile(String FileName)
    {
        try
        {
            FileReader fr = new FileReader(FileName);
            StringBuffer sb = new StringBuffer();
            int symbol;
            while((symbol = fr.read()) != -1)
            {
                sb.append((char)symbol);
            }
            Field.setText(sb.toString());
        }
        catch (FileNotFoundException ex)
        {
            JOptionPane.showMessageDialog(null, "�� ������� ����� ����.", "������", JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException ex)
        {
            JOptionPane.showMessageDialog(null, "�� ������� ��������� ����. �������� ���� ���������� ��� ������ ��� ��������.", "������", JOptionPane.ERROR_MESSAGE);
        }
    }

	// ��������� ���������� Field � ����
	public void writeToFile(String FileName)
    {
		String strText = Field.getText().trim();
		try
		{
			FileWriter fw = new FileWriter(FileName);
			fw.write(strText);
			fw.flush();
			fw.close();
			JOptionPane.showMessageDialog(null, "���� �������.", "�������", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (IOException ex)
		{
			JOptionPane.showMessageDialog(null, "�� ������� �������� ����.\n�������� ���� ��� ������� �������� �� ������.", "������", JOptionPane.ERROR_MESSAGE);
		}
    }
}

// �������� �����
class TextToGraphic
{
	static String FNames[][];
	static String PropNames[][];
	static String RelNames[];

	static ProtoFigure ProtoFigures[];
	static int FCount = 0;
	static int PCount = 0;
	static int RCount = 0;

	// --------------- ��������� (���� ���������, ������� �������� ���� ��� ��� ������ ���������) ------------------

	// ----- ��������� ���������
	// �����-���� ������
	static String SOME_FIGURE = "";

	// �����-���� ����-��������
	static String SOME_PREPROPERTY = "";

	// �����-���� ��������� ����� ��������
	static String SOME_RELATION = "";

	// ----- ���������� ���������
	// ���������� ������
	final static String WORD_CHAR = "[�-��-�a-zA-Z]";

	// �����
	final static String NUMBER_ST = "[0-9]+";

	// ����� � �����
	static String HAS_NVERTS = "";

	// ��������� �����
	static String MANYFIGURES = "";

	// ������ � ������
	static String FIGURE_IN_FIGURE = "";

	// �����������
	final static String PROPOSITION = WORD_CHAR+"[^.]*(\\.|$)";

	// ����� ���������� �������������� ����������
	static String ANY_FIGURE;

	// ����� ��������� ����� ��������
	static String ANY_RELATION = "";

	// ����-��������
	static String ANY_PREPROPERTY = "";
	static String ANY_PREPROPERTY2 = "";

	// ����-�������� � �������� ������
	static String PROPERTY_FIGURE;

	// ����������� ��� ������� ����������
	public static void main(String[] args) throws ClassNotFoundException
	{
		int error = 0;

		String jdbcLibraryName = "sqlite-jdbc-3.7.2.jar";
		String dbName = "onto.db";

		if (!(new File(jdbcLibraryName)).exists())
		{
			// ����������� ����������
			JOptionPane.showMessageDialog(null, "����������� ������������ ������ � ����������� ���������� sqlite-jdbc-3.7.2.jar\n���������� ����� ������� �� ���������� ������ https://bitbucket.org/xerial/sqlite-jdbc/downloads/sqlite-jdbc-3.7.2.jar", "������", JOptionPane.ERROR_MESSAGE);
			error = 3;
		}
		else // ���� ���������� �������
		{
			Log("�������� � ����� ������");

			// ���� ���� �� ����������
			if ((new File(dbName)).exists())
			{
				// �������� ������� ���������� �� ��
				if (!readDataBase())
				{
					// �� �����
					error = 1;
					TextToGraphic.Log("�� ������� ��������� ���������� �� ��.\n�������� ��� ���� ����������.");
					JOptionPane.showMessageDialog(null, "�� ������� ��������� ���������� �� ��.\n�������� ��� ���� ����������.", "������", JOptionPane.ERROR_MESSAGE);
				}
			}
			else
			{
				TextToGraphic.Log("�� ����� ���� ���� ������. ������� ������������ ����������� ����..");
				// �������� ������������ ����������� ����
				if (createNewDataBase())
				{
					// ���� ����� �������, ������� ������������
					if (!readDataBase())
					{
						// ��������������� �� �� ������������� ����������� ���������
						// ����� � ������� �� ������
						TextToGraphic.Log("�� ������� ���������� ��������������� ��. ���������� ����� �������.");
						JOptionPane.showMessageDialog(null, "�� ������� ���������� ��������������� ��. ���������� ����� �������.", "������", JOptionPane.ERROR_MESSAGE);
						error = 2;
					}
					else
					{
						TextToGraphic.Log("�������!");
						// ��� ������� ������������ �� � ������� �� �� ����������
						error = 0;
					}
				}
				else // ���� � ��� �� ����� ������� ����� ���� ��
				{
					try
					{
						// �������� �� ����������� ������ � �������
						FileWriter fw = new FileWriter("testexamplefile.test");
						fw.write(" ");
						fw.flush();
						fw.close();
						File f = new File("testexamplefile.test");
						f.delete();

						TextToGraphic.Log("�� ������� ������������ ��. ������� ���� ���� ������ � ������������� ����������.");
						JOptionPane.showMessageDialog(null, "�� ������� ������������ ��. ������� ���� ���� ������ � ������������� ����������.", "������", JOptionPane.ERROR_MESSAGE);
						error = 1;
					}
					catch (IOException ex)
					{
						TextToGraphic.Log("�� ������� ������������ ��. ������� ������� �� ������.");
						JOptionPane.showMessageDialog(null, "�� ������� ������������ ��. ������� ������� �� ������.", "������", JOptionPane.ERROR_MESSAGE);
						error = 4;
					}
				}
			}
		}

		if (error == 0)
		{
			Log("�������� ����");
			// ������ ����� ���� � ��������� ���������
			Window1 f = new Window1("�������");
			f.setSize(980, 450);
			f.setVisible(true);
			Log("���������� ������ � �����");
		}
		else
		{
			// ���� ���������� �� ������� ���������
			System.exit(0);
		}
	}

	// ������ ����������� ������ � ��
	public static boolean createNewDataBase() throws ClassNotFoundException
	{
		int error = 0;

		// ���������� ������� ��� ������ � SQLite
		Class.forName("org.sqlite.JDBC");

		Connection connection = null;
		try
		{
			// ������ ����������� � ��
			connection = DriverManager.getConnection("jdbc:sqlite:onto.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);

			// ���������� ������� FIGURES, ���� ����� ����
			statement.executeUpdate("drop table if exists FIGURES");
			// ������ �������
			statement.executeUpdate("create table FIGURES (ID_FIG integer, PARENT integer, NAME string, PADEGE_TYPE integer)");
			// ��������� ������
			statement.executeUpdate("insert into FIGURES values(0, -1, '�����', 1)");
			statement.executeUpdate("insert into FIGURES values(1, 0, '����', 0)");
			statement.executeUpdate("insert into FIGURES values(2, 1, '������', 0)");
			statement.executeUpdate("insert into FIGURES values(3, 2, '����', 0)");
			statement.executeUpdate("insert into FIGURES values(4, 0, '�������', 0)");
			statement.executeUpdate("insert into FIGURES values(5, 4, '��������������', 0)");
			statement.executeUpdate("insert into FIGURES values(6, 5, '�������������', 0)");
			statement.executeUpdate("insert into FIGURES values(7, 6, '�������', 0)");
			statement.executeUpdate("insert into FIGURES values(8, 4, '�����������', 0)");
			statement.executeUpdate("insert into FIGURES values(9, 4, '�������������', 0)");
			// ���� ���������������� �������
			statement.executeUpdate("insert into FIGURES values(10, 4, '�����', 1)");

			// ���������� ������� PADEGES, ���� ����� ����
			statement.executeUpdate("drop table if exists PADEGES");
			// ������ �������
			statement.executeUpdate("create table PADEGES (PADEGE_TYPE integer, INAME string, RNAME string, DNAME string, VNAME string, TNAME string, PNAME string)");
			// ��������� ������
			statement.executeUpdate("insert into PADEGES values(0, '', '�', '�', '', '��', '�')");
			statement.executeUpdate("insert into PADEGES values(1, '�', '�', '�', '�', '��', '�')");

			// ���������� ������� PROPERTIES, ���� ����� ����
			statement.executeUpdate("drop table if exists PROPERTIES");
			// ������ �������
			statement.executeUpdate("create table PROPERTIES (ID_PROP integer, ID_FIG integer, PROPERTY integer, VALUE integer)");
			// ��������� ������
			statement.executeUpdate("insert into PROPERTIES values(0, 1, 2, 1)");
			statement.executeUpdate("insert into PROPERTIES values(1, 1, 3, 0)");
			statement.executeUpdate("insert into PROPERTIES values(2, 2, 1, 1)");
			statement.executeUpdate("insert into PROPERTIES values(3, 3, 1, 2)");
			statement.executeUpdate("insert into PROPERTIES values(4, 4, 2, 1)");
			statement.executeUpdate("insert into PROPERTIES values(5, 4, 4, 3)");
			statement.executeUpdate("insert into PROPERTIES values(6, 5, 3, 4)");
			statement.executeUpdate("insert into PROPERTIES values(7, 6, 1, 1)");
			statement.executeUpdate("insert into PROPERTIES values(8, 7, 1, 2)");
			statement.executeUpdate("insert into PROPERTIES values(9, 8, 3, 3)");
			statement.executeUpdate("insert into PROPERTIES values(10, 9, 4, 5)");
			// ���� ���������������� �������
			statement.executeUpdate("insert into PROPERTIES values(11, 10, 20, 0)");

			// ���������� ������� PROPNAMES, ���� ����� ����
			statement.executeUpdate("drop table if exists PROPNAMES");
			// ������ �������
			statement.executeUpdate("create table PROPNAMES (PROPERTY integer, NAME string)");
			// ��������� ������
			statement.executeUpdate("insert into PROPNAMES values(0, '���������|���������|���������|���������|���������!����������|����������!�������|�������|�������|�������')");
			statement.executeUpdate("insert into PROPNAMES values(1, '�� ������������|�� ������������!������������|�����������|������������|������������|������������|�������������')");
			statement.executeUpdate("insert into PROPNAMES values(2, '����������|�����������|�� ���������|�� ��������|�� ���������!���������|�������|���������|��������|���������')");
			statement.executeUpdate("insert into PROPNAMES values(3, '�����|�������|�������|��������|�������!����|����|�����|�������|�������|������')");
			statement.executeUpdate("insert into PROPNAMES values(4, '������� ��� �������|������� ��� �������|������� ��� �������')");
			statement.executeUpdate("insert into PROPNAMES values(5, '������� ��������|������� ��������|������� ��������')");
			// ���� ���������������� �������
			statement.executeUpdate("insert into PROPNAMES values(20, '�����������')");

			// ���������� ������� RELATIONS, ���� ����� ����
			statement.executeUpdate("drop table if exists RELATIONS");
			// ������ �������
			statement.executeUpdate("create table RELATIONS (RELATION integer, NAME string)");
			// ��������� ������
			statement.executeUpdate("insert into RELATIONS values(1, '� |������ ')");
			statement.executeUpdate("insert into RELATIONS values(2, '���������� ')");
			statement.executeUpdate("insert into RELATIONS values(3, '������ �� |��� ')");
			statement.executeUpdate("insert into RELATIONS values(4, '����� �� |��� ')");
			statement.executeUpdate("insert into RELATIONS values(5, '����� �� |����� ')");
			statement.executeUpdate("insert into RELATIONS values(6, '������ �� |������ ')");
		}
		catch(SQLException e)
		{
			error = 1;
			// �� ������� �������� ���������� � ��
			//System.err.println(e.getMessage());
		}
		finally
		{
			try
			{
				if (connection != null)
					connection.close();
			}
			catch(SQLException e)
			{
				// ���� �� ������� ������� ����������� � ��
				//System.err.println(e);
			}
			finally
			{
				if (error != 0)
					return false;
				else
					return true;
			}
		}
	}

	// ��������� ������ �� ��
	public static boolean readDataBase() throws ClassNotFoundException
	{
		int error = 0;

		// ���������� ������� ��� ������ � SQLite
		Class.forName("org.sqlite.JDBC");

		Connection connection = null;
		try
		{
			// ������ ����������� � ��
			connection = DriverManager.getConnection("jdbc:sqlite:onto.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);

			// ����� ���������� ����� � ��
			ResultSet rs = statement.executeQuery("select count(*) as COUNT from FIGURES");
			while(rs.next())
			{
				FCount = rs.getInt("COUNT");
			}

			// �������� ������ ��� ������
			FNames = new String[FCount][8];
			ProtoFigures = new ProtoFigure[FCount];

			// �������� ��� ���������� � ������� �� ������� FIGURES
			int id;
			String name;
			SOME_FIGURE = "";
			rs = statement.executeQuery("select FIG.ID_FIG, FIG.PARENT, FIG.NAME, PAD.INAME, PAD.RNAME, PAD.DNAME, PAD.VNAME, PAD.TNAME, PAD.PNAME from FIGURES as FIG, PADEGES as PAD where FIG.PADEGE_TYPE = PAD.PADEGE_TYPE");
			int count = 0;
			while(rs.next())
			{
				id = rs.getInt("ID_FIG");
				FNames[id][0] = rs.getString("NAME");
				FNames[id][1] = rs.getString("INAME");
				FNames[id][2] = rs.getString("RNAME");
				FNames[id][3] = rs.getString("DNAME");
				FNames[id][4] = rs.getString("VNAME");
				FNames[id][5] = rs.getString("TNAME");
				FNames[id][6] = rs.getString("PNAME");
				FNames[id][7] = FNames[id][1] + "|" + FNames[id][2] + "|" + FNames[id][3] + "|" + FNames[id][4] + "|" + FNames[id][5] + "|" + FNames[id][6];

				SOME_FIGURE += (id > 0 ? ")|((" : "(") + FNames[id][0] + ")(" + FNames[id][7] + ")";
				ProtoFigures[id] = new ProtoFigure(id, rs.getInt("PARENT"));

				count++;
			}

			// ����� ���������� ��� �����
			rs = statement.executeQuery("select max(PROPERTY) as MAX from PROPNAMES");
			while(rs.next())
			{
				PCount = rs.getInt("MAX") + 1;
				if (PCount > Property.EMPTY_PROPERTY)
					PCount += Property.EMPTY_PROPERTY - 19;
			}

			// �������� ������
			PropNames = new String[PCount][3];

			// �������� ��� ����� ������� �����
			rs = statement.executeQuery("select * from PROPNAMES");
			String NextString;
			while(rs.next())
			{
				// ������ ID
				id = rs.getInt("PROPERTY");
				// ����������� ID
				if (id > Property.EMPTY_PROPERTY)
					id += Property.EMPTY_PROPERTY - 19;

				NextString = rs.getString("NAME");
				int i = 0;
				while (hasStringLikeThis(NextString, "!") && i < 3)
				{
					PropNames[id][i] = getStartStringLikeThis(NextString, "!");
					if (i < 3 || i > 5)
						SOME_PREPROPERTY += (id > 0 || i > 0 ? "|" : "") + PropNames[id][i];
					NextString = getEndStringLikeThis(NextString, "!");
					i++;
				}
				PropNames[id][i] = NextString;
				if (i < 3 || i > 5)
					SOME_PREPROPERTY += (id > 0 || i > 0 ? "|" : "") + PropNames[id][i];
			}

			// �������� ��� �������� �����
			rs = statement.executeQuery("select ID_FIG, PROPERTY, VALUE from PROPERTIES");
			int property;
			while(rs.next())
			{
				property = rs.getInt("PROPERTY");
				if (property > Property.EMPTY_PROPERTY)
					property += Property.EMPTY_PROPERTY - 19;
				ProtoFigures[rs.getInt("ID_FIG")].addProperty(new Property(property, rs.getInt("VALUE")));
			}

			// ��������� ��� ������ ����������� �������� ���� � ���������
			for (int i = 0; i < FCount; i++)
			{
				// ���� ��� �� ������� �����
				if (ProtoFigures[i].parent() != -1)
				{	// ��������� �������� �� �����������
					ProtoFigures[i].addPropertiesFromAnotherPF(ProtoFigures[ProtoFigures[i].parent()]);
				}
			}

			// ����� ���������� ��������� � ��
			rs = statement.executeQuery("select count(*) as COUNT from RELATIONS");
			while(rs.next())
			{
				RCount = rs.getInt("COUNT");
			}

			// �������� ������ ��� ���������
			RelNames = new String[RCount];

			// �������� ��� ���������� � ���������� ����� �������� �� ������� RELATIONS
			rs = statement.executeQuery("select * from RELATIONS");
			while(rs.next())
			{
				id = rs.getInt("RELATION") - 1;
				RelNames[id] = rs.getString("NAME");
				SOME_RELATION += (id > 0 ? "|" : "") + RelNames[id];
			}

			// ����� ���������� ���������, ��������� �� ������ �� ��
			SOME_FIGURE = "(("+SOME_FIGURE+"))";
			ANY_FIGURE = "(^|[\\s])+"+SOME_FIGURE+"([\\s]|\\.|,|$)";
			ANY_RELATION = "(^)((,|[\\s]|[^.])*[\\s])*("+SOME_RELATION+")[^.]*"+SOME_FIGURE+"([\\s]|\\.|,|$)";
			ANY_PREPROPERTY = "(^|[\\s])+("+SOME_PREPROPERTY+")([\\s]|\\.|,|$)+";
			ANY_PREPROPERTY2 = "(^|[\\s])*("+SOME_PREPROPERTY+")([\\s]|\\.|,|$)+";
			PROPERTY_FIGURE = "[\\s]*(("+SOME_PREPROPERTY+")[^.]*)+[\\s]+"+SOME_FIGURE+"([\\s]|\\.|,|$)";
			HAS_NVERTS = "(^)(,|[\\s])*("+PropNames[3][0]+")[\\s]+[0-9]+[\\s]+("+PropNames[3][1]+")([\\s]|\\.|,|$)";
			MANYFIGURES = "(^)(,|[\\s])*[0-9]+(("+SOME_PREPROPERTY+")[^.]*)*("+SOME_FIGURE+")([\\s]|\\.|,|$)";
		}
		catch(SQLException e)
		{
			error = 1;
			// �� ������� ������� ���������� �� ��
			//System.err.println(e.getMessage());
		}
		finally
		{
			try
			{
				if (connection != null)
					connection.close();
			}
			catch(SQLException e)
			{
				// ���� �� ������� ������� ����������� � ��
				//System.err.println(e);
			}
			finally
			{
				if (error != 0)
					return false;
				else
					return true;
			}
		}
	}

	// ����� ���� � ��������� ������� � �������
	public static void Log(String text)
	{
		System.out.println(new java.text.SimpleDateFormat("HH:mm:ss,S").format(java.util.Calendar.getInstance().getTime())+" Log: "+text);
	}

	// ������������� �����
	public static String AnalyseText(String st, TextArea ta, FiguresMass fr)
	{
		// �������� ��� �����������
		for (int i = 0; i < getCountOfStringsLikeThis(st, PROPOSITION); i++)
		{
			// ��������� ����������� � ����������
			String Propn = getStringLikeThis(st, PROPOSITION, i);

			// ���� ���� ���������� �����-���� �����
			if (hasStringLikeThis(Propn, ANY_FIGURE))
			{
				// ������� ����������� � TextArea
				ta.append("�����������: "+Propn+"\n");

				// ���������� ������
				String Next_String = Propn;
				// �������������� ������
				String This_String;
				// ������ �������� ������
				String FigureName;
				// ������ �������� ��������
				String PropertyName;
				// ��������������� ������
				String nextCroppedString;
				// ������ �� ��� ������ � ���������
				boolean isFirstFigure = true;

				// ��� ������ ������
				for (int j = 0; j < getCountOfStringsLikeThis(Propn, ANY_FIGURE); j++)
				{
					This_String = getStartIncStringLikeThis(Next_String, ANY_FIGURE);
					Next_String = getEndStringLikeThis(Next_String, ANY_FIGURE);

					// ������ ������ � ������� � ��������
					FigureName = getStringLikeThis(getStringLikeThis(Propn, ANY_FIGURE, j), SOME_FIGURE);
					Figure thisFigure = new Figure(ProtoFigures[getFigureID(FigureName)]);
					ta.append("���������� ������: "+FigureName+"\n");

					// ���� ���������� ����-��������
					if (hasStringLikeThis(This_String, PROPERTY_FIGURE))
					{
						// ������� ��� ����-��������
						String PropertiesString = "�������� ������ \""+FigureName+"\": ";
						for (int k = 0; k < getCountOfStringsLikeThis(This_String, ANY_PREPROPERTY2); k++)
						{
							// ������� ���, ��������� �� ��������
							PropertyName = getStringLikeThis(getStringLikeThis(This_String, ANY_PREPROPERTY2, k), SOME_PREPROPERTY);
							// ���� ��� ������ ��������
							Property newProperty = getPropertyByName(PropertyName);
							if ((newProperty.type() < 3 || newProperty.type() > 5) && newProperty.type() != Property.EMPTY_PROPERTY)
							{
								// ��������� ������ ��������
								thisFigure.addProperty(newProperty);
								PropertiesString += PropertyName + " ";
							}
						}
						ta.append(PropertiesString+"\n");
					}

					// �������� �� ��������� ������ ����� �� ��������� ������
					nextCroppedString = getStartIncStringLikeThis(Next_String, ANY_FIGURE);
					// ���� ���������� ���������� � ���������� ������
					if (hasStringLikeThis(nextCroppedString, HAS_NVERTS))
					{
						int numvert = Integer.parseInt(getStringLikeThis(getStringLikeThis(Next_String, HAS_NVERTS), NUMBER_ST));
						thisFigure.addProperty(new Property(3, numvert));
						ta.append("������ \""+FigureName+"\" ����� "+numvert+" ������.\n");

						// ������� ������, ����� �� ���������� �������� ������ ���
						Next_String = getEndStringLikeThis(Next_String, HAS_NVERTS);
					}

					// ���������� ����� ����� ������
					if (thisFigure.refreshClass())
					{	// ���� ������ �������� �����, �� �������� �� ����
						ta.append("��� �������� ��������: "+FNames[thisFigure.fclass()][0]+FNames[thisFigure.fclass()][1]+"\n");
					}

					// �������, ��� �� ���������� � ���������� �����
					if (hasStringLikeThis(This_String, MANYFIGURES))
					{
						String FigureName2 = getStringLikeThis(getStringLikeThis(This_String, MANYFIGURES), SOME_FIGURE);
						if (FigureName.equals(FigureName2))
						{
							ta.append("���\n");
							int fCount = Integer.parseInt(getStringLikeThis(getStringLikeThis(This_String, MANYFIGURES), NUMBER_ST));
							for (int k = 0; k < fCount - 1; k++)
							{
								fr.insertFigure(thisFigure);
							}
						}
					}

					// ���� ��� ������ ������ � ���������
					if (!isFirstFigure)
					{
						// ��������� ���������� � ������ � ��������� ������������ ���������
						fr.getFigure(fr.count() - 1).addSecondFigure(thisFigure);
						isFirstFigure = true;
					}
					else if (hasStringLikeThis(nextCroppedString, ANY_RELATION))
					{	// ���� ���������� ���������� �� ��������� ���� ������ � ������ �������

						// ������ ������ ������ � ���������
						fr.insertFigure(thisFigure);
						// �������� ��� ���������
						int relation = getRelationByName(getStringLikeThis(getStringLikeThis(Next_String, ANY_RELATION), SOME_RELATION));
						// ������������� ��� ���������
						fr.getFigure(fr.count() - 1).setType(relation);

						// ������� ������, ����� �� ���������� �������� ������ ���
						Next_String = getEndStringLikeThis(Next_String, SOME_RELATION);

						isFirstFigure = false;
					}
					else // ���� ��� ������� ��������� ������
					{
						// ��������� ������ � ������ �� ���������
						fr.insertFigure(thisFigure);
					}
				}
			}
		}

		return "���������� �����������: "+getCountOfStringsLikeThis(st, PROPOSITION);
	}

	// �������� �� ����� ���������, ��������������� �������?
	private static boolean hasStringLikeThis(String st, String mask)
	{
		Pattern p = Pattern.compile(mask, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(st.toLowerCase());
		while (m.find())
		{
			return true;
		}
		return false;
	}

	// ������� ���������� ���������, ��������������� �������
	private static int getCountOfStringsLikeThis(String st, String mask)
	{
		int count = 0;
		Pattern p = Pattern.compile(mask, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(st.toLowerCase());
		while (m.find())
		{
			count++;
		}
		return count;
	}

	// ������� ������ ���������, ������� ������������� �������
	private static String getStringLikeThis(String st, String mask)
	{
		Pattern p = Pattern.compile(mask, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(st.toLowerCase());
		while (m.find())
		{
			return m.group();
		}
		return "";
	}

	// (�������) ������� n-��� ��������� (������� � ����), ������� ������������� �������
	private static String getStringLikeThis(String st, String mask, int ordNumber)
	{
		int count = 0;
		Pattern p = Pattern.compile(mask, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(st.toLowerCase());
		while (m.find())
		{
			if (count == ordNumber)
				return m.group();

			count++;
		}
		return "";
	}

	// ������ ������ ������� ������ ��������� n-���� ���������, ���������������� �������
	private static int getStartPosStringLikeThis(String st, String mask, int ordNumber)
	{
		int count = 0;
		Pattern p = Pattern.compile(mask, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(st.toLowerCase());
		while (m.find())
		{
			if (count == ordNumber)
			{
				return m.start();
			}

			count++;
		}
		return -1;
	}

	// ������ ������ ������� ����� ��������� n-���� ���������, ���������������� �������
	private static int getEndPosStringLikeThis(String st, String mask, int ordNumber)
	{
		int count = 0;
		Pattern p = Pattern.compile(mask, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(st.toLowerCase());
		while (m.find())
		{
			if (count == ordNumber)
			{
				return m.end();
			}

			count++;
		}
		return -1;
	}

	// ������� ������, ������� ������������ ������� ���������, ���������������� �������
	private static String getStartStringLikeThis(String st, String mask)
	{
		int start = getStartPosStringLikeThis(st, mask, 0);
		if (start != -1)
			return st.substring(0, start);
		else
			return st;
	}

	// (�������) ������� ������, ������� ������������ n-���� ���������, ���������������� �������
	private static String getStartStringLikeThis(String st, String mask, int ordNumber)
	{
		int start = getStartPosStringLikeThis(st, mask, ordNumber);
		if (start != -1)
			return st.substring(0, start);
		else
			return st;
	}

	// ������� ������, ������� ������������ (� ��������) ������� ���������, ���������������� �������
	private static String getStartIncStringLikeThis(String st, String mask)
	{
		int end = getEndPosStringLikeThis(st, mask, 0);
		if (end != -1)
			return st.substring(0, end);
		else
			return st;
	}

	// (�������) ������� ������, ������� ������������ (� ��������) n-���� ���������, ���������������� �������
	private static String getStartIncStringLikeThis(String st, String mask, int ordNumber)
	{
		int end = getEndPosStringLikeThis(st, mask, ordNumber);
		if (end != -1)
			return st.substring(0, end);
		else
			return st;
	}

	// ������� ������, ������� ��������� ����� ������� ���������, ���������������� �������
	private static String getEndStringLikeThis(String st, String mask)
	{
		int end = getEndPosStringLikeThis(st, mask, 0);
		if (end != -1)
			return st.substring(end, st.length());
		else
			return st;
	}

	// (�������) ������� ������, ������� ��������� ����� n-���� ���������, ���������������� �������
	private static String getEndStringLikeThis(String st, String mask, int ordNumber)
	{
		int end = getEndPosStringLikeThis(st, mask, ordNumber);
		if (end != -1)
			return st.substring(end, st.length());
		else
			return st;
	}

	// ���������� ID ������
	private static int getFigureID(String FigureName)
	{
		for (int i  = 0; i < FCount; i++)
		{
			// ���� �������� ������ � ����� �� �������
			if (hasStringLikeThis(FigureName, "("+FNames[i][0]+")("+FNames[i][7]+")"))
				return i;
		}
		return -1;
	}

	// ���������� �������� �� ��������
	private static Property getPropertyByName(String PropertyName)
	{
		for (int i = 0; i < PCount; i++)
		{
			if (i < 3 || i > 5)
			{
				for (int j = 0; j < 3; j++)
				{
					if (PropNames[i][j] != null && hasStringLikeThis(PropertyName, PropNames[i][j]))
					{
						return new Property(i, j);
					}
				}
			}
		}

		return new Property();
	}

	// ���������� ��������� �� ��������
	private static int getRelationByName(String RelName)
	{
		for (int i = 0; i < RCount; i++)
		{
			// ���� �������� ��������� � ����� �� �������
			if (hasStringLikeThis(RelName, RelNames[i]))
				return i + 1;
		}
		return -1;
	}
}

// ���������� �������
class ActLis implements ActionListener, TextListener
{
	private TextArea tf;
	private Label lb;
	private TextArea ta;
	private FiguresMass fr;
	private Window1 window;

	// ��� �������� �����������
	ActLis(Window1 window)
	{
		this.window = window;
		this.tf = window.Field;
		this.lb = window.Label1;
		this.ta = window.Area;
		this.fr = window.Figures;
	}

	// ��� ������� ������ "����������"
	public void actionPerformed(ActionEvent ae)
	{
		ta.replaceRange("", 0, 10000);
		fr.reset();
		lb.setText(TextToGraphic.AnalyseText(tf.getText(), ta, fr));
		window.update(window.getGraphics());
		window.paint(window.getGraphics());
	}

	// ��� ��������� ������
	public void textValueChanged(TextEvent e)
	{
		if (window.CB.getState())
		{
			ta.replaceRange("", 0, 10000);
			fr.reset();
			lb.setText(TextToGraphic.AnalyseText(tf.getText(), ta, fr));
			window.update(window.getGraphics());
			window.paint(window.getGraphics());
		}
	}
}