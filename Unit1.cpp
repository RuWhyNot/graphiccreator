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
    Name = "Фигура";
  }
};

// инициализируем массивы
void __fastcall TForm1::FormCreate(TObject *Sender)
{
  FiguresN[0] = "Треугольник";
  FiguresN[1] = "Квадрат";
  FiguresN[2] = "Многоугольник";
  FiguresN[3] = "Круг";
  FiguresN[4] = "Эллипс";
  FiguresN[5] = "Параллелограмм";
  FiguresN[6] = "Трапеция";

  SizesN[0] = "Маленький";
  SizesN[1] = "Нормальный";
  SizesN[2] = "Большой";
}

// проверяем количество углов в фигуре
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

// проверяем правильность фигуры
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
  // заполняем выпадающий список фигур
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

