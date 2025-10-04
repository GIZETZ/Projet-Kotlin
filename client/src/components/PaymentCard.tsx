import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { format } from "date-fns";
import { fr } from "date-fns/locale";
import { Banknote, CreditCard, Smartphone } from "lucide-react";

export interface Payment {
  id: number;
  payerName: string;
  montant: number;
  datePaiement: Date;
  methode: string;
  commentaire?: string;
}

interface PaymentCardProps {
  payment: Payment;
  onClick?: () => void;
}

const methodIcons: Record<string, typeof Banknote> = {
  especes: Banknote,
  "mobile money": Smartphone,
  carte: CreditCard,
};

export default function PaymentCard({ payment, onClick }: PaymentCardProps) {
  const MethodIcon = methodIcons[payment.methode.toLowerCase()] || Banknote;

  return (
    <Card
      className="p-4 hover-elevate active-elevate-2 cursor-pointer"
      onClick={onClick}
      data-testid={`card-payment-${payment.id}`}
    >
      <div className="flex items-start justify-between gap-4">
        <div className="flex-1 space-y-1">
          <div className="flex items-center gap-2">
            <h4 className="font-semibold" data-testid="text-payer-name">{payment.payerName}</h4>
          </div>
          <div className="flex items-center gap-2 text-sm text-muted-foreground">
            <MethodIcon className="w-3 h-3" />
            <span>{payment.methode}</span>
            <span>•</span>
            <span>{format(payment.datePaiement, "d MMM yyyy", { locale: fr })}</span>
          </div>
          {payment.commentaire && (
            <p className="text-sm text-muted-foreground line-clamp-1">{payment.commentaire}</p>
          )}
        </div>
        <div className="text-right">
          <p className="text-lg font-bold tabular-nums text-chart-2" data-testid="text-payment-amount">
            {new Intl.NumberFormat("fr-FR", { style: "currency", currency: "XAF" }).format(payment.montant)}
          </p>
        </div>
      </div>
    </Card>
  );
}
