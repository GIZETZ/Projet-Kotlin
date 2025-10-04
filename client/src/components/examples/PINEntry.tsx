import PINEntry from '../PINEntry';

export default function PINEntryExample() {
  return (
    <PINEntry
      title="Entrez votre code PIN"
      onSubmit={(pin) => console.log('PIN entered:', pin)}
    />
  );
}
