//---------------------------------------------------------------------------

#ifndef Unit1H
#define Unit1H
//---------------------------------------------------------------------------
#include <Classes.hpp>
#include <Controls.hpp>
#include <StdCtrls.hpp>
#include <Forms.hpp>
#include <ComCtrls.hpp>
//---------------------------------------------------------------------------
class TForm1 : public TForm
{
__published:	// IDE-managed Components
  TPageControl *PageControl1;
  TTabSheet *TabSheet1;
  TComboBox *CB_Figures;
  TLabel *Label1;
  TEdit *Ed_VertCount;
  TLabel *Label2;
  TCheckBox *ChB_Valid;
  TLabel *Label3;
  TComboBox *CB_Size;
  TMemo *Memo1;
  TButton *Button1;
  void __fastcall FormCreate(TObject *Sender);
  void __fastcall FormActivate(TObject *Sender);
  void __fastcall CB_FiguresChange(TObject *Sender);
  void __fastcall Button1Click(TObject *Sender);
private:	// User declarations
public:		// User declarations
  __fastcall TForm1(TComponent* Owner);
};
//---------------------------------------------------------------------------
extern PACKAGE TForm1 *Form1;
//---------------------------------------------------------------------------
#endif
 