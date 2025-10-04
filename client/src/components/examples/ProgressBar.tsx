import ProgressBar from '../ProgressBar';

export default function ProgressBarExample() {
  return (
    <div className="space-y-6 p-6 max-w-md">
      <ProgressBar current={15000} target={50000} />
      <ProgressBar current={42000} target={50000} />
      <ProgressBar current={50000} target={50000} />
    </div>
  );
}
