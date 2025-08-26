package com.project.service;

import com.project.entity.Address;
import com.project.entity.User;
import com.project.exception.ResourceNotFoundException;
import com.project.exception.ResourceAlreadyExists;
import com.project.repository.AddressRepository;
import com.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public List<Address> getUserAddresses(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return addressRepository.findByUserId(userId);
    }

    public Address getAddressById(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
    }

    public Address createAddress(Long userId, Address address) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        address.setUser(user);

        // If this is the first address, make it default
        List<Address> existingAddresses = addressRepository.findByUserId(userId);
        if (existingAddresses.isEmpty()) {
            address.setIsDefault(true);
        }

        // If this address is set as default, unset other defaults
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            unsetOtherDefaults(userId);
        }

        return addressRepository.save(address);
    }

    public Address updateAddress(Long userId, Long addressId, Address addressDetails) {
        Address address = getAddressById(addressId);
        
        // Check if user owns this address
        if (!address.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Address", "id", addressId);
        }

        if (addressDetails.getFirstName() != null) {
            address.setFirstName(addressDetails.getFirstName());
        }
        if (addressDetails.getLastName() != null) {
            address.setLastName(addressDetails.getLastName());
        }
        if (addressDetails.getCompany() != null) {
            address.setCompany(addressDetails.getCompany());
        }
        if (addressDetails.getAddressLine1() != null) {
            address.setAddressLine1(addressDetails.getAddressLine1());
        }
        if (addressDetails.getAddressLine2() != null) {
            address.setAddressLine2(addressDetails.getAddressLine2());
        }
        if (addressDetails.getCity() != null) {
            address.setCity(addressDetails.getCity());
        }
        if (addressDetails.getState() != null) {
            address.setState(addressDetails.getState());
        }
        if (addressDetails.getPostalCode() != null) {
            address.setPostalCode(addressDetails.getPostalCode());
        }
        if (addressDetails.getCountry() != null) {
            address.setCountry(addressDetails.getCountry());
        }
        if (addressDetails.getPhone() != null) {
            address.setPhone(addressDetails.getPhone());
        }
        if (addressDetails.getType() != null) {
            address.setType(addressDetails.getType());
        }

        // Handle default address logic
        if (Boolean.TRUE.equals(addressDetails.getIsDefault())) {
            unsetOtherDefaults(userId);
            address.setIsDefault(true);
        }

        return addressRepository.save(address);
    }

    public void deleteAddress(Long userId, Long addressId) {
        Address address = getAddressById(addressId);
        
        // Check if user owns this address
        if (!address.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Address", "id", addressId);
        }

        // If this was the default address, make another address default
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            List<Address> remainingAddresses = addressRepository.findByUserId(userId);
            remainingAddresses.remove(address);
            if (!remainingAddresses.isEmpty()) {
                Address newDefault = remainingAddresses.get(0);
                newDefault.setIsDefault(true);
                addressRepository.save(newDefault);
            }
        }

        addressRepository.delete(address);
    }

    public Address setDefaultAddress(Long userId, Long addressId) {
        Address address = getAddressById(addressId);
        
        // Check if user owns this address
        if (!address.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Address", "id", addressId);
        }

        unsetOtherDefaults(userId);
        address.setIsDefault(true);
        return addressRepository.save(address);
    }

    public Address getDefaultAddress(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        return addressRepository.findByUserIdAndIsDefaultTrue(userId).stream().findFirst().get();
    }

    public List<Address> getAddressesByType(Long userId, String type) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return addressRepository.findByUserIdAndType(userId, type);
    }

    public Address getDefaultAddressByType(Long userId, String type) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        List<Address> addresses = addressRepository.findByUserIdAndTypeAndIsDefaultTrue(userId, type);
        return addresses.isEmpty() ? null : addresses.get(0);
    }

    private void unsetOtherDefaults(Long userId) {
        List<Address> defaultAddresses = addressRepository.findByUserIdAndIsDefaultTrue(userId);
        for (Address addr : defaultAddresses) {
            addr.setIsDefault(false);
            addressRepository.save(addr);
        }
    }

    public boolean validateAddress(Address address) {
        // Basic validation
        if (address.getAddressLine1() == null || address.getAddressLine1().trim().isEmpty()) {
            return false;
        }
        if (address.getCity() == null || address.getCity().trim().isEmpty()) {
            return false;
        }
        if (address.getState() == null || address.getState().trim().isEmpty()) {
            return false;
        }
        if (address.getPostalCode() == null || address.getPostalCode().trim().isEmpty()) {
            return false;
        }
        if (address.getCountry() == null || address.getCountry().trim().isEmpty()) {
            return false;
        }
        return true;
    }

    public long getAddressCount(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return addressRepository.findByUserId(userId).size();
    }
}
