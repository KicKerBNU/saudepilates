import { jsPDF } from 'jspdf';

const FONT_SIZE = 11;
const TITLE_SIZE = 14;
const MARGIN = 20;
const LINE_HEIGHT = 6;

function formatDateForPdf(iso) {
  if (!iso) return '—';
  const d = String(iso).slice(0, 10);
  const [y, m, day] = d.split('-');
  return `${day}/${m}/${y}`;
}

function addSection(doc, y, title, content) {
  if (y > 270) {
    doc.addPage();
    y = 20;
  }
  doc.setFontSize(TITLE_SIZE);
  doc.setFont(undefined, 'bold');
  doc.text(title, MARGIN, y);
  y += LINE_HEIGHT;
  doc.setFontSize(FONT_SIZE);
  doc.setFont(undefined, 'normal');
  const lines = doc.splitTextToSize(content || '—', 170);
  doc.text(lines, MARGIN, y);
  y += lines.length * LINE_HEIGHT + 8;
  return y;
}

export function generateSingleAnamnesisPdf(studentName, data, t) {
  const doc = new jsPDF();
  let y = 20;

  doc.setFontSize(16);
  doc.setFont(undefined, 'bold');
  doc.text(t('anamnesis.title'), MARGIN, y);
  y += 10;

  doc.setFontSize(FONT_SIZE);
  doc.setFont(undefined, 'bold');
  doc.text(`${t('anamnesis.student')}: ${studentName}`, MARGIN, y);
  y += LINE_HEIGHT;
  if (data.performedAt) {
    doc.setFont(undefined, 'normal');
    const dateStr = formatDateForPdf(data.performedAt);
    doc.text(`${t('anamnesis.performedAt')}: ${dateStr}`, MARGIN, y);
    y += LINE_HEIGHT + 6;
  } else {
    y += 6;
  }

  y = addSection(doc, y, t('anamnesis.patientIdentification'), data.patientIdentification);
  y = addSection(doc, y, t('anamnesis.mainComplaint'), data.mainComplaint);
  y = addSection(doc, y, t('anamnesis.currentDiseaseHistory'), data.currentDiseaseHistory);
  y = addSection(doc, y, t('anamnesis.socialHistory'), data.socialHistory);
  y = addSection(doc, y, t('anamnesis.perimeterData'), data.perimeterData);
  y = addSection(doc, y, t('anamnesis.posturalAssessment'), data.posturalAssessment);
  y = addSection(doc, y, t('anamnesis.treatment'), data.treatment);

  const safeName = (studentName || 'anamnese').replace(/[^a-zA-Z0-9\u00C0-\u024F\s]/g, '').trim().slice(0, 40);
  doc.save(`Anamnese_${safeName}.pdf`);
}

export function generateAllAnamnesisPdf(studentsWithAnamnesis, studentsMap, t) {
  const doc = new jsPDF();
  let y = 20;
  let isFirst = true;

  for (const item of studentsWithAnamnesis) {
    if (!isFirst) {
      doc.addPage();
      y = 20;
    }
    isFirst = false;

    const studentName = studentsMap[item.studentId]?.name || item.studentId;
    const data = item;

    doc.setFontSize(16);
    doc.setFont(undefined, 'bold');
    doc.text(t('anamnesis.title'), MARGIN, y);
    y += 10;

    doc.setFontSize(FONT_SIZE);
    doc.setFont(undefined, 'bold');
    doc.text(`${t('anamnesis.student')}: ${studentName}`, MARGIN, y);
    y += LINE_HEIGHT;
    if (data.performedAt) {
      doc.setFont(undefined, 'normal');
      doc.text(`${t('anamnesis.performedAt')}: ${formatDateForPdf(data.performedAt)}`, MARGIN, y);
      y += LINE_HEIGHT + 6;
    } else {
      y += 6;
    }

    y = addSection(doc, y, t('anamnesis.patientIdentification'), data.patientIdentification);
    y = addSection(doc, y, t('anamnesis.mainComplaint'), data.mainComplaint);
    y = addSection(doc, y, t('anamnesis.currentDiseaseHistory'), data.currentDiseaseHistory);
    y = addSection(doc, y, t('anamnesis.socialHistory'), data.socialHistory);
    y = addSection(doc, y, t('anamnesis.perimeterData'), data.perimeterData);
    y = addSection(doc, y, t('anamnesis.posturalAssessment'), data.posturalAssessment);
    y = addSection(doc, y, t('anamnesis.treatment'), data.treatment);
  }

  doc.save(t('anamnesis.allAnamnesisPdf') + '.pdf');
}

export function getPrintContent(studentName, data, t) {
  const sections = [
    { title: t('anamnesis.performedAt'), content: data.performedAt ? formatDateForPdf(data.performedAt) : '—' },
    { title: t('anamnesis.patientIdentification'), content: data.patientIdentification || '—' },
      { title: t('anamnesis.mainComplaint'), content: data.mainComplaint || '—' },
      { title: t('anamnesis.currentDiseaseHistory'), content: data.currentDiseaseHistory || '—' },
      { title: t('anamnesis.socialHistory'), content: data.socialHistory || '—' },
      { title: t('anamnesis.perimeterData'), content: data.perimeterData || '—' },
      { title: t('anamnesis.posturalAssessment'), content: data.posturalAssessment || '—' },
    { title: t('anamnesis.treatment'), content: data.treatment || '—' }
  ];
  return {
    title: t('anamnesis.title'),
    studentLabel: t('anamnesis.student'),
    studentName,
    sections
  };
}
