import PaymentCard from '../PaymentCard';

export default function PaymentCardExample() {
  const mockPayments = [
    {
      id: 1,
      payerName: "Jean Mukendi",
      montant: 2500,
      datePaiement: new Date(2025, 0, 15),
      methode: "Mobile Money",
      commentaire: "Paiement via Orange Money",
    },
    {
      id: 2,
      payerName: "Marie Kabongo",
      montant: 2500,
      datePaiement: new Date(2025, 0, 14),
      methode: "Espèces",
    },
  ];

  return (
    <div className="space-y-3 p-6 max-w-lg">
      {mockPayments.map((payment) => (
        <PaymentCard
          key={payment.id}
          payment={payment}
          onClick={() => console.log('Payment clicked:', payment.id)}
        />
      ))}
    </div>
  );
}
