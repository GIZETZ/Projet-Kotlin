import { useState } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Label } from "@/components/ui/label";
import { FileText, Image, FileSpreadsheet, Share2, Copy, Download } from "lucide-react";
import { SiWhatsapp } from "react-icons/si";

interface ExportModalProps {
  open: boolean;
  onClose: () => void;
  operationName: string;
  onExport: (format: string) => void;
}

export default function ExportModal({ open, onClose, operationName, onExport }: ExportModalProps) {
  const [format, setFormat] = useState<string>("text");

  const formats = [
    { value: "text", label: "Texte", icon: FileText, description: "Copier/coller dans WhatsApp" },
    { value: "pdf", label: "PDF", icon: FileText, description: "Document portable" },
    { value: "image", label: "Image", icon: Image, description: "Capture d'écran" },
    { value: "csv", label: "CSV", icon: FileSpreadsheet, description: "Export pour Excel" },
  ];

  const handleExport = () => {
    onExport(format);
    onClose();
  };

  const handleShare = () => {
    console.log("Share via WhatsApp:", format);
    onClose();
  };

  const selectedFormat = formats.find(f => f.value === format);

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle className="text-2xl">Partager {operationName}</DialogTitle>
        </DialogHeader>

        <div className="space-y-6">
          <RadioGroup value={format} onValueChange={setFormat}>
            <div className="space-y-3">
              {formats.map((fmt) => {
                const Icon = fmt.icon;
                return (
                  <div key={fmt.value} className="flex items-center space-x-3">
                    <RadioGroupItem value={fmt.value} id={fmt.value} data-testid={`radio-format-${fmt.value}`} />
                    <Label
                      htmlFor={fmt.value}
                      className="flex items-center gap-3 flex-1 cursor-pointer p-3 rounded-lg hover-elevate"
                    >
                      <Icon className="w-5 h-5 text-muted-foreground" />
                      <div className="flex-1">
                        <p className="font-medium">{fmt.label}</p>
                        <p className="text-sm text-muted-foreground">{fmt.description}</p>
                      </div>
                    </Label>
                  </div>
                );
              })}
            </div>
          </RadioGroup>

          <div className="pt-4 space-y-3">
            <Button onClick={handleShare} className="w-full bg-[#25D366] hover:bg-[#1fa855] text-white" data-testid="button-share-whatsapp">
              <SiWhatsapp className="w-5 h-5 mr-2" />
              Partager via WhatsApp
            </Button>
            
            <div className="grid grid-cols-2 gap-3">
              <Button onClick={handleExport} variant="outline" data-testid="button-copy">
                {format === "text" ? <Copy className="w-4 h-4 mr-2" /> : <Download className="w-4 h-4 mr-2" />}
                {format === "text" ? "Copier" : "Télécharger"}
              </Button>
              <Button onClick={handleExport} variant="outline" data-testid="button-share-other">
                <Share2 className="w-4 h-4 mr-2" />
                Autres
              </Button>
            </div>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}
