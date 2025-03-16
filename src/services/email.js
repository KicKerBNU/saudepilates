import emailjs from '@emailjs/browser';

/**
 * Service for handling email functionality using EmailJS
 */
export const EmailService = {
  /**
   * Sends an auto-reply email using EmailJS
   * @param {Object} contactData - Contact form data
   * @param {string} contactData.name - Contact's name
   * @param {string} contactData.email - Contact's email
   * @param {string} contactData.subject - Message subject
   * @param {string} contactData.message - Message content
   * @param {Object} companyData - Company information
   * @param {string} companyData.name - Company name
   * @param {string} companyData.logo - Company logo URL
   * @param {string} companyData.phone - Company phone
   * @param {string} companyData.whatsapp - Company WhatsApp
   * @param {string} companyData.website - Company website URL
   * @param {string} companyData.instagram - Company Instagram URL
   * @param {string} companyData.facebook - Company Facebook URL
   * @returns {Promise} - Promise with the result of the email sending operation
   */
  sendAutoReply: async (contactData, companyData) => {
    try {
      // Make sure required values exist
      if (!contactData || !contactData.email) {
        throw new Error('Recipient email is required');
      }

      // Get current year for template
      const currentYear = new Date().getFullYear();

      // Prepare template parameters
      const templateParams = {
        to_email: contactData.email,
        to_name: contactData.name || 'Cliente',
        from_name: companyData?.name || 'Saúde Pilates',
        subject: 'Recebemos sua mensagem - Saúde Pilates',
        name: contactData.name || 'Cliente',
        subject: contactData.subject || 'Contato via Website',
        message: contactData.message || '',
        company_logo: companyData?.logo || '',
        company_phone: companyData?.phone || '',
        company_whatsapp: companyData?.whatsapp || '',
        website_url: companyData?.website || '',
        instagram_url: companyData?.instagram || '',
        facebook_url: companyData?.facebook || '',
        whatsapp_url: companyData?.whatsapp ? `https://wa.me/${companyData.whatsapp.replace(/\D/g, '')}` : '',
        current_year: currentYear.toString(),
      };

      // Use EmailJS credentials - the same as in the Contact component
      const serviceId = 'service_6tlvlos';
      const templateId = 'template_p043u8o';
      const publicKey = '_DcuCvNtpYC2WN3Ia';

      // Send the email
      const response = await emailjs.send(
        serviceId,
        templateId,
        templateParams,
        publicKey
      );

      console.log('Email sent successfully!', response);
      return response;
    } catch (error) {
      console.error('Failed to send email:', error);
      throw error;
    }
  },

  /**
   * Initializes EmailJS with your public key
   * @param {string} publicKey - Your EmailJS public key
   */
  init: (publicKey) => {
    emailjs.init(publicKey);
  }
};
