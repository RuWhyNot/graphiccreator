//---------------------------------------------------------------------------

#include <vcl.h>
#include <Math>
#pragma hdrstop

#include "Unit1.h"
#include "Unit2.h"
//---------------------------------------------------------------------------
#pragma package(smart_init)
#pragma resource "*.dfm"
TForm1 *Form1;
//---------------------------------------------------------------------------
__fastcall TForm1::TForm1(TComponent* Owner)
  : TForm(Owner)
{
}
//---------------------------------------------------------------------------

const FIGURES_COUNT = 7;
const SIZES_COUNT = 3;

String FiguresN[FIGURES_COUNT];
String SizesN[SIZES_COUNT];

enum Size
{
  SZ_Min,
  SZ_Mid,
  SZ_Big
};

class Figure
{
public:
  String Name;

  void Draw();

  Figure()
  {
    Name = "������";
  }
};

// �������������� �������
void __fastcall TForm1::FormCreate(TObject *Sender)
{
  FiguresN[0] = "�����������";
  FiguresN[1] = "�������";
  FiguresN[2] = "�������������";
  FiguresN[3] = "����";
  FiguresN[4] = "������";
  FiguresN[5] = "��������������";
  FiguresN[6] = "��������";

  SizesN[0] = "���������";
  SizesN[1] = "����������";
  SizesN[2] = "�������";
}

// ��������� ���������� ����� � ������
int CheckVCount(int figureType)
{
  switch (figureType)
  {
    case 0:
      return 3;
    case 1:
    case 5:
    case 6:
      return 4;
    case 3:
    case 4:
      return 0;
    case 2:
    default:
      return -1;
  }
}

// ��������� ������������ ������
bool CheckValid(int figureType)
{
  switch (figureType)
  {
    case 1:
    case 3:
      return true;
    default:
      return false;
  }
}
//---------------------------------------------------------------------------
void __fastcall TForm1::FormActivate(TObject *Sender)
{
  // ��������� ���������� ������ �����
  for (int i = 0; i < FIGURES_COUNT; i++)
  {
    Form1->CB_Figures->AddItem(FiguresN[i], 0);
  }

  for (int i = 0; i < SIZES_COUNT; i++)
  {
    Form1->CB_Size->AddItem(SizesN[i], 0);
  }
}
//---------------------------------------------------------------------------
void __fastcall TForm1::CB_FiguresChange(TObject *Sender)
{
  int itemIdx = CB_Figures->ItemIndex;
  if (CheckVCount(itemIdx) != -1)
  {
    Ed_VertCount->Enabled = false;
    Ed_VertCount->Text = IntToStr(CheckVCount(itemIdx));
  }
  else
  {
    Ed_VertCount->Enabled = true;
    Ed_VertCount->Text = IntToStr(random(5)+3);
  }

  if (CheckValid(itemIdx))
  {
    ChB_Valid->Enabled = false;
    ChB_Valid->Checked = true;
  }
  else
  {
    ChB_Valid->Enabled = true;
    ChB_Valid->Checked = false;
  }
}
//---------------------------------------------------------------------------
void __fastcall TForm1::Button1Click(TObject *Sender)
{
  std::getline(fs,str,',');  
}
//---------------------------------------------------------------------------

